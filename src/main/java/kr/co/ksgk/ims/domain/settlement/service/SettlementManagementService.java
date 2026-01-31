package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.settlement.dto.request.SettlementDetailUpdateRequest;
import kr.co.ksgk.ims.domain.settlement.dto.response.InvoiceResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementDetailResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementResponse.*;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.*;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementManagementService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final SettlementTypeRepository settlementTypeRepository;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;
    private final ChargeCategoryRepository chargeCategoryRepository;
    private final CompanyItemChargeMappingRepository companyItemChargeMappingRepository;

    public SettlementResponse getSettlementByCompany(int year, int month, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        Settlement settlement = settlementRepository.findByYearAndMonthAndCompany(year, month, company)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId());

        return buildSettlementResponse(settlement, details);
    }

    public List<SettlementResponse> getSettlementsByYearAndMonth(int year, int month) {
        List<Settlement> settlements = settlementRepository.findByYearAndMonth(year, month);

        return settlements.stream()
                .map(settlement -> {
                    List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId());
                    return buildSettlementResponse(settlement, details);
                })
                .toList();
    }

    @Transactional
    public SettlementDetailResponse updateSettlementDetail(Long detailId, SettlementDetailUpdateRequest request) {
        SettlementDetail detail = settlementDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_DETAIL_NOT_FOUND));

        // CONFIRMED 상태면 수정 불가
        if (detail.getSettlement().getStatus() == SettlementStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_CONFIRMED);
        }

        detail.update(request.quantity(), request.unitPrice(), request.amount(), request.note());

        return SettlementDetailResponse.from(detail);
    }

    @Transactional
    public SettlementResponse confirmSettlement(Long settlementId, Long memberId) {
        Member confirmedBy = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.confirm(confirmedBy);

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlementId);

        return buildSettlementResponse(settlement, details);
    }

    @Transactional
    public SettlementResponse revertToDraft(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.revertToDraft();

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlementId);

        return buildSettlementResponse(settlement, details);
    }

    // --- 피벗 응답 빌드 ---

    private SettlementResponse buildSettlementResponse(Settlement settlement, List<SettlementDetail> details) {
        // 1. 고유 품목 추출 (열 헤더)
        List<ProductColumn> products = details.stream()
                .map(d -> new ProductColumn(d.getProduct().getId(), d.getProduct().getName()))
                .distinct()
                .toList();

        List<Long> productIds = products.stream().map(ProductColumn::id).toList();

        // 2. 룩업맵: itemId -> productId -> SettlementDetail
        Map<Long, Map<Long, SettlementDetail>> detailLookup = new HashMap<>();
        for (SettlementDetail d : details) {
            detailLookup
                    .computeIfAbsent(d.getSettlementItem().getId(), k -> new HashMap<>())
                    .put(d.getProduct().getId(), d);
        }

        // 3. 전체 구조 조회
        List<SettlementType> types = settlementTypeRepository.findAllWithHierarchy();

        // 4. TypeRow 빌드
        List<TypeRow> typeRows = types.stream()
                .map(type -> buildTypeRow(type, productIds, detailLookup))
                .toList();

        // 5. 총합
        int totalAmount = typeRows.stream()
                .mapToInt(tr -> tr.subtotalAmount() != null ? tr.subtotalAmount() : 0)
                .sum();

        return new SettlementResponse(
                settlement.getId(),
                settlement.getYear(),
                settlement.getMonth(),
                settlement.getCompany().getId(),
                settlement.getCompany().getName(),
                settlement.getStatus(),
                settlement.getConfirmedAt(),
                settlement.getConfirmedBy() != null ? settlement.getConfirmedBy().getName() : null,
                products,
                typeRows,
                totalAmount
        );
    }

    private TypeRow buildTypeRow(SettlementType type, List<Long> productIds,
                                 Map<Long, Map<Long, SettlementDetail>> detailLookup) {
        List<CategoryRow> categoryRows = type.getCategories().stream()
                .sorted(Comparator.comparing(SettlementCategory::getDisplayOrder))
                .map(cat -> buildCategoryRow(cat, productIds, detailLookup))
                .toList();

        List<ItemRow> directItemRows = type.getDirectItems().stream()
                .sorted(Comparator.comparing(SettlementItem::getDisplayOrder))
                .map(item -> buildItemRow(item, productIds, detailLookup))
                .toList();

        int subtotal = 0;
        for (CategoryRow cr : categoryRows) {
            for (ItemRow ir : cr.items()) {
                subtotal += ir.totalAmount() != null ? ir.totalAmount() : 0;
            }
        }
        for (ItemRow ir : directItemRows) {
            subtotal += ir.totalAmount() != null ? ir.totalAmount() : 0;
        }

        return new TypeRow(type.getId(), type.getName(), categoryRows, directItemRows, subtotal);
    }

    private CategoryRow buildCategoryRow(SettlementCategory category, List<Long> productIds,
                                         Map<Long, Map<Long, SettlementDetail>> detailLookup) {
        List<ItemRow> itemRows = category.getItems().stream()
                .sorted(Comparator.comparing(SettlementItem::getDisplayOrder))
                .map(item -> buildItemRow(item, productIds, detailLookup))
                .toList();

        return new CategoryRow(category.getId(), category.getName(), itemRows);
    }

    private ItemRow buildItemRow(SettlementItem item, List<Long> productIds,
                                 Map<Long, Map<Long, SettlementDetail>> detailLookup) {
        Map<Long, SettlementDetail> productMap = detailLookup.getOrDefault(item.getId(), Map.of());

        List<ProductCell> cells = productIds.stream()
                .map(pid -> {
                    SettlementDetail d = productMap.get(pid);
                    if (d == null) {
                        return new ProductCell(pid, null, null, null, null, null);
                    }
                    return new ProductCell(pid, d.getId(), d.getQuantity(), d.getUnitPrice(), d.getAmount(), d.getNote());
                })
                .toList();

        List<UnitInfo> units = item.getUnits().stream()
                .sorted(Comparator.comparing(SettlementUnit::getDisplayOrder))
                .map(u -> new UnitInfo(u.getId(), u.getName(), u.getPrice()))
                .toList();

        int totalQuantity = cells.stream()
                .mapToInt(c -> c.quantity() != null ? c.quantity() : 0)
                .sum();
        int totalAmount = cells.stream()
                .mapToInt(c -> c.amount() != null ? c.amount() : 0)
                .sum();

        return new ItemRow(item.getId(), item.getName(), item.getCalculationType(),
                units, cells, totalQuantity, totalAmount);
    }

    // --- 청구서 (변경 없음) ---

    public InvoiceResponse getInvoiceByCompany(int year, int month, Long companyId) {
        // 해당 업체의 확정된 정산서만 조회
        List<Settlement> settlements = settlementRepository.findByYearAndMonthAndCompanyIdAndStatus(
                year, month, companyId, SettlementStatus.CONFIRMED);

        if (settlements.isEmpty()) {
            return new InvoiceResponse(year, month, List.of());
        }

        // 모든 ChargeCategory 조회 (청구서 열 구성용)
        List<ChargeCategory> chargeCategories = chargeCategoryRepository.findAll();
        Map<Long, String> chargeCategoryNames = chargeCategories.stream()
                .collect(Collectors.toMap(ChargeCategory::getId, ChargeCategory::getName));

        // CompanyItemChargeMapping 조회 (SettlementItem -> ChargeCategory 매핑)
        List<CompanyItemChargeMapping> mappings = companyItemChargeMappingRepository.findByCompanyId(companyId);
        Map<Long, Long> itemToChargeCategory = mappings.stream()
                .collect(Collectors.toMap(
                        m -> m.getSettlementItem().getId(),
                        m -> m.getChargeCategory().getId()));

        // 업체 정보
        Company company = settlements.get(0).getCompany();

        // 모든 정산서의 detail을 수집하여 브랜드별로 그룹화
        List<SettlementDetail> allDetails = new ArrayList<>();
        for (Settlement settlement : settlements) {
            allDetails.addAll(settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId()));
        }

        // 브랜드별 그룹화 (SettlementDetail의 product.getBrand() 기준)
        Map<Brand, List<SettlementDetail>> detailsByBrand = allDetails.stream()
                .collect(Collectors.groupingBy(d -> d.getProduct().getBrand()));

        List<InvoiceResponse.BrandInvoice> brandInvoices = new ArrayList<>();
        Map<String, Integer> companyCategoryTotal = new LinkedHashMap<>();

        // 카테고리 초기화
        for (ChargeCategory category : chargeCategories) {
            companyCategoryTotal.put(category.getName(), 0);
        }

        for (Map.Entry<Brand, List<SettlementDetail>> entry : detailsByBrand.entrySet()) {
            Brand brand = entry.getKey();
            List<SettlementDetail> brandDetails = entry.getValue();

            // 품목별 그룹화
            Map<Long, List<SettlementDetail>> detailsByProduct = brandDetails.stream()
                    .collect(Collectors.groupingBy(d -> d.getProduct().getId()));

            List<InvoiceResponse.ProductInvoice> productInvoices = new ArrayList<>();

            for (Map.Entry<Long, List<SettlementDetail>> productEntry : detailsByProduct.entrySet()) {
                List<SettlementDetail> productDetails = productEntry.getValue();
                String productName = productDetails.get(0).getProduct().getName();

                Map<String, Integer> productCategories = new LinkedHashMap<>();
                for (ChargeCategory category : chargeCategories) {
                    productCategories.put(category.getName(), 0);
                }

                for (SettlementDetail detail : productDetails) {
                    Long itemId = detail.getSettlementItem().getId();
                    Long chargeCategoryId = itemToChargeCategory.get(itemId);

                    if (chargeCategoryId != null) {
                        String categoryName = chargeCategoryNames.get(chargeCategoryId);
                        if (categoryName != null && detail.getAmount() != null) {
                            productCategories.merge(categoryName, detail.getAmount(), Integer::sum);
                            companyCategoryTotal.merge(categoryName, detail.getAmount(), Integer::sum);
                        }
                    }
                }

                productInvoices.add(new InvoiceResponse.ProductInvoice(
                        productEntry.getKey(), productName, productCategories));
            }

            brandInvoices.add(new InvoiceResponse.BrandInvoice(brand.getId(), brand.getName(), productInvoices));
        }

        int companyTotal = companyCategoryTotal.values().stream().mapToInt(Integer::intValue).sum();

        List<InvoiceResponse.CompanyInvoice> companyInvoices = List.of(
                new InvoiceResponse.CompanyInvoice(
                        company.getId(), company.getName(),
                        brandInvoices, companyCategoryTotal, companyTotal));

        return new InvoiceResponse(year, month, companyInvoices);
    }
}
