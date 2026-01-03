package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.delivery.repository.DeliveryRepository;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceProductRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.entity.DailyStockCache;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionStatus;
import kr.co.ksgk.ims.domain.stock.repository.DailyStockCacheRepository;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.domain.stock.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeStockService {

    private final StockRepository stockRepository;
    private final TransactionRepository transactionRepository;
    private final DeliveryRepository deliveryRepository;
    private final InvoiceProductRepository invoiceProductRepository;
    private final DailyStockCacheRepository cacheRepository;

    private static final long TODAY_CACHE_TTL = 3600L; // 1시간 (초)

    public Optional<DailyStockCache> getTodayStock(Product product) {
        LocalDate today = LocalDate.now();
        return getTodayStock(product, today);
    }

    public Optional<DailyStockCache> getTodayStock(Product product, LocalDate targetDate) {
        // 오늘 데이터만 캐싱 (과거 데이터는 DailyStock 테이블에서 조회)
        if (!targetDate.equals(LocalDate.now())) {
            return Optional.empty(); // 과거 데이터는 캐싱하지 않음
        }

        // 캐시에서 먼저 조회
        Optional<DailyStockCache> cached = cacheRepository.findByProductIdAndStockDate(product.getId(), targetDate);
        if (cached.isPresent()) {
            log.debug("캐시에서 재고 데이터 반환 - Product: {}, Date: {}", product.getId(), targetDate);
            return cached;
        }

        // 캐시에 없으면 실시간 계산
        log.debug("실시간 재고 계산 - Product: {}, Date: {}", product.getId(), targetDate);
        DailyStockCache calculatedStock = calculateRealTimeStock(product, targetDate);
        
        // 계산 결과를 캐시에 저장
        cacheRepository.save(calculatedStock);
        log.debug("캐시에 재고 데이터 저장 완료 - Product: {}, Date: {}", product.getId(), targetDate);
        
        return Optional.of(calculatedStock);
    }

    public List<DailyStockCache> getTodayStockForProducts(List<Product> products) {
        LocalDate today = LocalDate.now();
        List<DailyStockCache> allStocks = new ArrayList<>();
        
        for (Product product : products) {
            Optional<DailyStockCache> cached = cacheRepository.findByProductIdAndStockDate(product.getId(), today);
            if (cached.isPresent()) {
                log.debug("캐시에서 재고 데이터 반환 - Product: {}, Date: {}", product.getId(), today);
                allStocks.add(cached.get());
            } else {
                // 캐시에 없으면 실시간 계산
                log.debug("실시간 재고 계산 - Product: {}, Date: {}", product.getId(), today);
                DailyStockCache calculatedStock = calculateRealTimeStock(product, today);
                cacheRepository.save(calculatedStock);
                allStocks.add(calculatedStock);
            }
        }
        
        return allStocks;
    }

    private DailyStockCache calculateRealTimeStock(Product product, LocalDate targetDate) {
        // 전날 DailyStock에서 현재 재고 가져오기
        Integer previousStock = getPreviousStock(product, targetDate);

        // 해당 날짜의 Transaction, Delivery, InvoiceProduct 데이터 집계
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByProductAndUpdatedAtBetweenAndTransactionStatus(
                product, startOfDay, endOfDay, TransactionStatus.CONFIRMED);

        List<Delivery> deliveries = deliveryRepository.findByRawProductProductMappingsProductAndCreatedAtBetween(
                product, startOfDay, endOfDay);

        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findByProductAndInvoiceCreatedAtBetween(
                product, startOfDay, endOfDay);

                int incoming = calculateIncoming(transactions);
                int returnIncoming = calculateReturnIncoming(transactions, invoiceProducts);
                int outgoing = calculateOutgoing(transactions);
                int coupangFulfillment = calculateCoupangFulfillment(transactions);
                int naverFulfillment = calculateNaverFulfillment(transactions);
                int deliveryOutgoing = calculateDeliveryOutgoing(deliveries, product);
                int redelivery = calculateRedelivery(transactions);
                int damaged = calculateDamaged(transactions);
                int disposal = calculateDisposal(transactions);
                int lost = calculateLost(transactions);
                int adjustment = calculateAdjustment(transactions);

        Integer currentStock = previousStock + incoming + returnIncoming
                - outgoing - coupangFulfillment - naverFulfillment - deliveryOutgoing - redelivery
                - damaged - disposal - lost + adjustment;

        return DailyStockCache.builder()
                .productId(product.getId())
                .stockDate(targetDate)
                .currentStock(currentStock)
                .incoming(incoming)
                .returnIncoming(returnIncoming)
                .outgoing(outgoing)
                .coupangFulfillment(coupangFulfillment)
                .naverFulfillment(naverFulfillment)
                .deliveryOutgoing(deliveryOutgoing)
                .redelivery(redelivery)
                .damaged(damaged)
                .disposal(disposal)
                .lost(lost)
                .adjustment(adjustment)
                .ttl(TODAY_CACHE_TTL)
                .build();
    }

    private Integer getPreviousStock(Product product, LocalDate targetDate) {
        // 전날의 DailyStock에서 현재 재고를 가져오기
        LocalDate previousDate = targetDate.minusDays(1);
        // 전날 재고 조회 (없으면 0)
        return stockRepository.findByProductAndStockDate(product, previousDate)
                .map(DailyStock::getCurrentStock)
                .orElse(0);
    }

    private Integer calculateIncoming(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "INCOMING".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateReturnIncoming(List<Transaction> transactions, List<InvoiceProduct> invoiceProducts) {
        int transactionReturn = transactions.stream()
                .filter(t -> "RETURN_INCOMING".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();

        int invoiceReturn = invoiceProducts.stream()
                .mapToInt(InvoiceProduct::getResalableQuantity)
                .sum();

        return transactionReturn + invoiceReturn;
    }

    private Integer calculateOutgoing(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "OUTGOING".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateCoupangFulfillment(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "COUPANG_FULFILLMENT".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateNaverFulfillment(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "NAVER_FULFILLMENT".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateDeliveryOutgoing(List<Delivery> deliveries, Product targetProduct) {
        return deliveries.stream()
                .flatMap(delivery -> delivery.getRawProduct().getProductMappings().stream()
                        .filter(mapping -> mapping.getProduct().equals(targetProduct))
                        .map(mapping -> delivery.getQuantity() * mapping.getQuantity()))
                .mapToInt(Integer::intValue)
                .sum();
    }

    private Integer calculateRedelivery(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "REDELIVERY".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateDamaged(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "DAMAGED".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateDisposal(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "DISPOSAL".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateLost(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "LOST".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    private Integer calculateAdjustment(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "ADJUSTMENT".equals(t.getTransactionType().getName()))
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    @Transactional
    public void invalidateTodayCache(Long productId) {
        LocalDate today = LocalDate.now();
        cacheRepository.deleteByProductIdAndStockDate(productId, today);
        log.debug("오늘 캐시 무효화 완료 - Product: {}, Date: {}", productId, today);
    }

    @Transactional
    public void invalidateAllCacheForProduct(Long productId) {
        cacheRepository.deleteByProductId(productId);
        log.debug("제품 전체 캐시 무효화 완료 - Product: {}", productId);
    }
}