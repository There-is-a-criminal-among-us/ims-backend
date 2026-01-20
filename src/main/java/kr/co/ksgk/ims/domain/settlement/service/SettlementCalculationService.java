package kr.co.ksgk.ims.domain.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.StorageType;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.*;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.entity.TransactionWork;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.domain.stock.repository.TransactionWorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SettlementCalculationService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final DeliverySheetRowRepository deliverySheetRowRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final TransactionWorkRepository transactionWorkRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void calculateSettlements(int year, int month) {
        // 모든 품목 조회 (브랜드별로 그룹화)
        List<Product> allProducts = productRepository.findAll();
        Map<Brand, List<Product>> productsByBrand = allProducts.stream()
                .collect(Collectors.groupingBy(Product::getBrand));

        // 모든 SettlementItem 조회
        List<SettlementItem> allItems = settlementItemRepository.findAll();

        // 브랜드별 정산서 생성/갱신
        for (Map.Entry<Brand, List<Product>> entry : productsByBrand.entrySet()) {
            Brand brand = entry.getKey();
            List<Product> products = entry.getValue();

            // 기존 정산서 조회 또는 생성 (details fetch join으로 clearDetails 가능하도록)
            Settlement settlement = settlementRepository.findByYearAndMonthAndBrandWithDetails(year, month, brand)
                    .orElseGet(() -> settlementRepository.save(Settlement.create(year, month, brand)));

            // CONFIRMED 상태면 DRAFT로 되돌리기
            if (settlement.getStatus() == SettlementStatus.CONFIRMED) {
                settlement.revertToDraft();
            }

            // 기존 상세 삭제
            settlement.clearDetails();

            // 각 품목별, 항목별 계산
            for (Product product : products) {
                for (SettlementItem item : allItems) {
                    SettlementDetail detail = calculateDetail(year, month, product, item);
                    if (detail != null) {
                        settlement.addDetail(detail);
                    }
                }
            }
        }
    }

    private SettlementDetail calculateDetail(int year, int month, Product product, SettlementItem item) {
        CalculationType calculationType = item.getCalculationType();

        return switch (calculationType) {
            case MANUAL -> calculateManual(year, month, product, item);
            case STORAGE -> calculateStorage(year, month, product, item);
            case SIZE -> calculateSize(year, month, product, item);
            case RETURN_SIZE -> calculateReturnSize(year, month, product, item);
            case REMOTE_AREA -> calculateRemoteArea(year, month, product, item);
        };
    }

    private SettlementDetail calculateManual(int year, int month, Product product, SettlementItem item) {
        // TransactionWork에서 해당 월, 해당 품목, 해당 항목의 데이터 집계
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        // TransactionWork를 통해 해당 월의 데이터를 조회해야 함
        // TransactionWork -> Transaction -> workDate로 필터링
        // 여기서는 간단하게 구현 (실제로는 Query 최적화 필요)
        List<TransactionWork> works = transactionWorkRepository.findAll().stream()
                .filter(w -> w.getSettlementItem().getId().equals(item.getId()))
                .filter(w -> w.getTransaction().getProduct().getId().equals(product.getId()))
                .filter(w -> {
                    LocalDate workDate = w.getTransaction().getWorkDate();
                    return workDate != null &&
                            !workDate.isBefore(startDate) &&
                            !workDate.isAfter(endDate);
                })
                .toList();

        if (works.isEmpty()) {
            return null;
        }

        int totalQuantity = works.stream().mapToInt(TransactionWork::getQuantity).sum();
        int totalCost = works.stream().mapToInt(TransactionWork::getTotalCost).sum();

        // 평균 단가 계산
        int unitPrice = totalQuantity > 0 ? totalCost / totalQuantity : 0;

        return createDetail(product, item, totalQuantity, unitPrice, totalCost, null);
    }

    private SettlementDetail calculateStorage(int year, int month, Product product, SettlementItem item) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        // 일별 재고 조회
        List<DailyStock> dailyStocks = stockRepository.findAllByProductsAndDateBetween(
                List.of(product), startDate, endDate);

        if (dailyStocks.isEmpty()) {
            return null;
        }

        StorageType storageType = product.getStorageType();
        if (storageType == null) {
            return null;
        }

        int totalAmount = 0;
        int totalDays = dailyStocks.size();

        if (storageType == StorageType.CBM) {
            // CBM: Σ(일별 재고수) × cbm × storagePricePerCbm
            if (product.getCbm() == null || product.getStoragePricePerCbm() == null) {
                return null;
            }

            int totalStock = dailyStocks.stream().mapToInt(DailyStock::getCurrentStock).sum();
            BigDecimal cbm = product.getCbm();
            BigDecimal pricePerCbm = product.getStoragePricePerCbm();

            totalAmount = BigDecimal.valueOf(totalStock)
                    .multiply(cbm)
                    .multiply(pricePerCbm)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();

        } else if (storageType == StorageType.PALLET) {
            // PALLET: Σ(ceil(일별 재고수 / quantityPerPallet)) × storagePricePerPallet
            if (product.getQuantityPerPallet() == null || product.getStoragePricePerPallet() == null) {
                return null;
            }

            int quantityPerPallet = product.getQuantityPerPallet();
            BigDecimal pricePerPallet = product.getStoragePricePerPallet();

            int totalPallets = dailyStocks.stream()
                    .mapToInt(ds -> (int) Math.ceil((double) ds.getCurrentStock() / quantityPerPallet))
                    .sum();

            totalAmount = pricePerPallet.multiply(BigDecimal.valueOf(totalPallets))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        }

        if (totalAmount == 0) {
            return null;
        }

        return createDetail(product, item, totalDays, null, totalAmount, null);
    }

    private SettlementDetail calculateSize(int year, int month, Product product, SettlementItem item) {
        if (product.getSizeUnit() == null) {
            return null;
        }

        // 출고 건수 계산 (택배표에서)
        long outboundCount = deliverySheetRowRepository.countByYearAndMonthAndProductAndWorkType(
                year, month, product, WorkType.OUTBOUND);

        if (outboundCount == 0) {
            return null;
        }

        int unitPrice = product.getSizeUnit().getPrice();
        int totalAmount = unitPrice * (int) outboundCount;

        return createDetail(product, item, (int) outboundCount, unitPrice, totalAmount, null);
    }

    private SettlementDetail calculateReturnSize(int year, int month, Product product, SettlementItem item) {
        if (product.getReturnSizeUnit() == null) {
            return null;
        }

        // 반품 건수 계산 (택배표에서)
        long returnCount = deliverySheetRowRepository.countByYearAndMonthAndProductAndWorkType(
                year, month, product, WorkType.RETURN);

        if (returnCount == 0) {
            return null;
        }

        int unitPrice = product.getReturnSizeUnit().getPrice();
        int totalAmount = unitPrice * (int) returnCount;

        return createDetail(product, item, (int) returnCount, unitPrice, totalAmount, null);
    }

    private SettlementDetail calculateRemoteArea(int year, int month, Product product, SettlementItem item) {
        // 택배표에서 해당 품목의 REMOTE_AREA 비용 합산
        List<DeliverySheetRow> rows = deliverySheetRowRepository.findByYearAndMonthAndProduct(year, month, product);

        if (rows.isEmpty()) {
            return null;
        }

        String itemName = item.getName();
        int totalFee = 0;
        int count = 0;

        for (DeliverySheetRow row : rows) {
            if (row.getRemoteAreaFees() == null) {
                continue;
            }

            try {
                Map<String, Integer> fees = objectMapper.readValue(
                        row.getRemoteAreaFees(), new TypeReference<>() {});

                Integer fee = fees.get(itemName);
                if (fee != null && fee > 0) {
                    totalFee += fee;
                    count++;
                }
            } catch (JsonProcessingException e) {
                log.error("Failed to parse remote area fees JSON", e);
            }
        }

        if (totalFee == 0) {
            return null;
        }

        return createDetail(product, item, count, null, totalFee, null);
    }

    private SettlementDetail createDetail(Product product, SettlementItem item,
                                           Integer quantity, Integer unitPrice, Integer amount, String note) {
        // Settlement는 나중에 설정됨 (addDetail에서)
        return SettlementDetail.create(null, product, item, quantity, unitPrice, amount, note);
    }
}
