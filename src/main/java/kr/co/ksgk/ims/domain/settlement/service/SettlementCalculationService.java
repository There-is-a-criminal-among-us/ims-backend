package kr.co.ksgk.ims.domain.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.domain.product.entity.StorageType;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.product.repository.RawProductRepository;
import kr.co.ksgk.ims.domain.settlement.entity.*;
import kr.co.ksgk.ims.domain.settlement.repository.*;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.entity.DailyStockLot;
import kr.co.ksgk.ims.domain.stock.entity.TransactionWork;
import kr.co.ksgk.ims.domain.stock.repository.DailyStockLotRepository;
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
    private final SettlementItemRepository settlementItemRepository;
    private final DeliverySheetRowRepository deliverySheetRowRepository;
    private final ProductRepository productRepository;
    private final RawProductRepository rawProductRepository;
    private final StockRepository stockRepository;
    private final TransactionWorkRepository transactionWorkRepository;
    private final DailyStockLotRepository dailyStockLotRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void calculateSettlements(int year, int month) {
        // 모든 품목 조회 (업체별로 그룹화)
        List<Product> allProducts = productRepository.findAll();
        Map<Company, List<Product>> productsByCompany = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getBrand().getCompany()));

        // 모든 SettlementItem 조회
        List<SettlementItem> allItems = settlementItemRepository.findAll();

        // 업체별 정산서 생성/갱신
        for (Map.Entry<Company, List<Product>> entry : productsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<Product> products = entry.getValue();

            // 기존 정산서 조회 또는 생성 (details fetch join으로 clearDetails 가능하도록)
            Settlement settlement = settlementRepository.findByYearAndMonthAndCompanyWithDetails(year, month, company)
                    .orElseGet(() -> settlementRepository.save(Settlement.create(year, month, company)));

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
        long totalCost = works.stream().mapToLong(TransactionWork::getTotalCost).sum();

        // 평균 단가 계산
        int unitPrice = totalQuantity > 0 ? (int) (totalCost / totalQuantity) : 0;

        return createDetail(product, item, totalQuantity, unitPrice, totalCost, null);
    }

    private SettlementDetail calculateStorage(int year, int month, Product product, SettlementItem item) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        StorageType storageType = product.getStorageType();
        if (storageType == null) {
            return null;
        }

        // DailyStockLot 기반 계산 시도 (각 로트에 저장된 freePeriodDays 사용)
        List<DailyStockLot> dailyStockLots = dailyStockLotRepository.findByProductAndDateBetween(
                product, startDate, endDate);

        long totalAmount = 0L;
        int totalDays = 0;

        if (!dailyStockLots.isEmpty()) {
            // DailyStockLot 기반 계산 (무료 기간 적용)
            totalDays = (int) dailyStockLots.stream()
                    .map(DailyStockLot::getStockDate)
                    .distinct()
                    .count();

            if (storageType == StorageType.CBM) {
                if (product.getCbm() == null || product.getStoragePricePerCbm() == null) {
                    return null;
                }

                // 무료 기간 초과 로트만 합산 (각 로트의 자체 freePeriodDays 사용)
                long billableStock = dailyStockLots.stream()
                        .filter(dsl -> !dsl.isWithinFreePeriod())
                        .mapToLong(DailyStockLot::getQuantity)
                        .sum();

                BigDecimal cbm = product.getCbm();
                BigDecimal pricePerCbm = product.getStoragePricePerCbm();

                totalAmount = BigDecimal.valueOf(billableStock)
                        .multiply(cbm)
                        .multiply(pricePerCbm)
                        .setScale(0, RoundingMode.HALF_UP)
                        .longValue();

                log.debug("CBM 보관료 계산 - Product: {}, TotalLots: {}, BillableStock: {}, Amount: {}",
                        product.getId(), dailyStockLots.size(), billableStock, totalAmount);

            } else if (storageType == StorageType.PALLET) {
                if (product.getQuantityPerPallet() == null || product.getStoragePricePerPallet() == null) {
                    return null;
                }

                int quantityPerPallet = product.getQuantityPerPallet();
                BigDecimal pricePerPallet = product.getStoragePricePerPallet();

                // 일별로 그룹핑하여 과금 대상 수량 계산 후 팔렛 수 산출 (각 로트의 자체 freePeriodDays 사용)
                Map<LocalDate, Integer> dailyBillableQuantities = dailyStockLots.stream()
                        .filter(dsl -> !dsl.isWithinFreePeriod())
                        .collect(Collectors.groupingBy(
                                DailyStockLot::getStockDate,
                                Collectors.summingInt(DailyStockLot::getQuantity)
                        ));

                long totalPallets = dailyBillableQuantities.values().stream()
                        .mapToLong(qty -> (long) Math.ceil((double) qty / quantityPerPallet))
                        .sum();

                totalAmount = pricePerPallet.multiply(BigDecimal.valueOf(totalPallets))
                        .setScale(0, RoundingMode.HALF_UP)
                        .longValue();

                log.debug("PALLET 보관료 계산 - Product: {}, TotalPallets: {}, Amount: {}",
                        product.getId(), totalPallets, totalAmount);
            }
        } else {
            // DailyStockLot 데이터가 없으면 기존 DailyStock 방식으로 폴백 (무료 기간 미적용)
            List<DailyStock> dailyStocks = stockRepository.findAllByProductsAndDateBetween(
                    List.of(product), startDate, endDate);

            if (dailyStocks.isEmpty()) {
                return null;
            }

            totalDays = dailyStocks.size();
            log.debug("DailyStockLot 없음, DailyStock 폴백 - Product: {}, Days: {}", product.getId(), totalDays);

            if (storageType == StorageType.CBM) {
                if (product.getCbm() == null || product.getStoragePricePerCbm() == null) {
                    return null;
                }

                long totalStock = dailyStocks.stream().mapToLong(DailyStock::getCurrentStock).sum();
                BigDecimal cbm = product.getCbm();
                BigDecimal pricePerCbm = product.getStoragePricePerCbm();

                totalAmount = BigDecimal.valueOf(totalStock)
                        .multiply(cbm)
                        .multiply(pricePerCbm)
                        .setScale(0, RoundingMode.HALF_UP)
                        .longValue();

            } else if (storageType == StorageType.PALLET) {
                if (product.getQuantityPerPallet() == null || product.getStoragePricePerPallet() == null) {
                    return null;
                }

                int quantityPerPallet = product.getQuantityPerPallet();
                BigDecimal pricePerPallet = product.getStoragePricePerPallet();

                long totalPallets = dailyStocks.stream()
                        .mapToLong(ds -> (long) Math.ceil((double) ds.getCurrentStock() / quantityPerPallet))
                        .sum();

                totalAmount = pricePerPallet.multiply(BigDecimal.valueOf(totalPallets))
                        .setScale(0, RoundingMode.HALF_UP)
                        .longValue();
            }
        }

        if (totalAmount == 0) {
            return null;
        }

        return createDetail(product, item, totalDays, null, totalAmount, null);
    }

    private SettlementDetail calculateSize(int year, int month, Product product, SettlementItem item) {
        List<DeliverySheetRow> rows = deliverySheetRowRepository
                .findByYearAndMonthAndProductAndWorkType(year, month, product, WorkType.OUTBOUND);

        if (rows.isEmpty()) {
            return null;
        }

        // 수량: 각 row의 quantity 합산
        int quantity = rows.stream().mapToInt(DeliverySheetRow::getQuantity).sum();

        // costTarget인 row만 금액 계산 (quantity * unitPrice)
        long totalAmount = 0L;
        Integer unitPrice = null;
        for (DeliverySheetRow row : rows) {
            if (row.getCostTarget()) {
                RawProduct rp = rawProductRepository.findByName(row.getProductName()).orElse(null);
                if (rp != null && rp.getSizeUnit() != null) {
                    unitPrice = rp.getSizeUnit().getPrice();
                    totalAmount += (long) unitPrice * row.getQuantity();
                }
            }
        }

        if (totalAmount == 0 && unitPrice == null) {
            return createDetail(product, item, quantity, null, 0L, null);
        }

        return createDetail(product, item, quantity, unitPrice, totalAmount, null);
    }

    private SettlementDetail calculateReturnSize(int year, int month, Product product, SettlementItem item) {
        List<DeliverySheetRow> rows = deliverySheetRowRepository
                .findByYearAndMonthAndProductAndWorkType(year, month, product, WorkType.RETURN);

        if (rows.isEmpty()) {
            return null;
        }

        // 수량: 각 row의 quantity 합산
        int quantity = rows.stream().mapToInt(DeliverySheetRow::getQuantity).sum();

        // costTarget인 row만 금액 계산 (quantity * unitPrice)
        long totalAmount = 0L;
        Integer unitPrice = null;
        for (DeliverySheetRow row : rows) {
            if (row.getCostTarget()) {
                RawProduct rp = rawProductRepository.findByName(row.getProductName()).orElse(null);
                if (rp != null && rp.getReturnSizeUnit() != null) {
                    unitPrice = rp.getReturnSizeUnit().getPrice();
                    totalAmount += (long) unitPrice * row.getQuantity();
                }
            }
        }

        if (totalAmount == 0 && unitPrice == null) {
            return createDetail(product, item, quantity, null, 0L, null);
        }

        return createDetail(product, item, quantity, unitPrice, totalAmount, null);
    }

    private SettlementDetail calculateRemoteArea(int year, int month, Product product, SettlementItem item) {
        // 택배표에서 해당 품목의 REMOTE_AREA 비용 합산
        List<DeliverySheetRow> rows = deliverySheetRowRepository.findByYearAndMonthAndProduct(year, month, product);

        if (rows.isEmpty()) {
            return null;
        }

        String itemName = item.getName();
        long totalFee = 0L;
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
                                           Integer quantity, Integer unitPrice, Long amount, String note) {
        // Settlement는 나중에 설정됨 (addDetail에서)
        return SettlementDetail.create(null, product, item, quantity, unitPrice, amount, note);
    }
}
