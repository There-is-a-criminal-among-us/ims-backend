package kr.co.ksgk.ims.domain.settlement.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.settlement.dto.request.SettlementDetailUpdateRequest;
import kr.co.ksgk.ims.domain.settlement.dto.response.InvoiceResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementDetailResponse;
import kr.co.ksgk.ims.domain.settlement.dto.response.SettlementResponse;
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
    private final BrandRepository brandRepository;
    private final ChargeCategoryRepository chargeCategoryRepository;
    private final CompanyItemChargeMappingRepository companyItemChargeMappingRepository;

    public SettlementResponse getSettlementByBrand(int year, int month, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

        Settlement settlement = settlementRepository.findByYearAndMonthAndBrand(year, month, brand)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId());

        List<SettlementDetailResponse> detailResponses = details.stream()
                .map(SettlementDetailResponse::from)
                .toList();

        return SettlementResponse.from(settlement, detailResponses);
    }

    public List<SettlementResponse> getSettlementsByYearAndMonth(int year, int month) {
        List<Settlement> settlements = settlementRepository.findByYearAndMonth(year, month);

        return settlements.stream()
                .map(settlement -> {
                    List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId());
                    List<SettlementDetailResponse> detailResponses = details.stream()
                            .map(SettlementDetailResponse::from)
                            .toList();
                    return SettlementResponse.from(settlement, detailResponses);
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
    public SettlementResponse confirmSettlement(Long settlementId, Member confirmedBy) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.confirm(confirmedBy);

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlementId);
        List<SettlementDetailResponse> detailResponses = details.stream()
                .map(SettlementDetailResponse::from)
                .toList();

        return SettlementResponse.from(settlement, detailResponses);
    }

    @Transactional
    public SettlementResponse revertToDraft(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.revertToDraft();

        List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlementId);
        List<SettlementDetailResponse> detailResponses = details.stream()
                .map(SettlementDetailResponse::from)
                .toList();

        return SettlementResponse.from(settlement, detailResponses);
    }

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
        Company company = settlements.get(0).getBrand().getCompany();

        // 브랜드별 그룹화
        Map<Brand, List<Settlement>> settlementsByBrand = settlements.stream()
                .collect(Collectors.groupingBy(Settlement::getBrand));

        List<InvoiceResponse.BrandInvoice> brandInvoices = new ArrayList<>();
        Map<String, Integer> companyCategoryTotal = new LinkedHashMap<>();

        // 카테고리 초기화
        for (ChargeCategory category : chargeCategories) {
            companyCategoryTotal.put(category.getName(), 0);
        }

        for (Map.Entry<Brand, List<Settlement>> entry : settlementsByBrand.entrySet()) {
            Brand brand = entry.getKey();
            List<Settlement> brandSettlements = entry.getValue();

            List<InvoiceResponse.ProductInvoice> productInvoices = new ArrayList<>();

            for (Settlement settlement : brandSettlements) {
                List<SettlementDetail> details = settlementDetailRepository.findBySettlementIdWithDetails(settlement.getId());

                // 품목별 그룹화
                Map<Long, List<SettlementDetail>> detailsByProduct = details.stream()
                        .collect(Collectors.groupingBy(d -> d.getProduct().getId()));

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
