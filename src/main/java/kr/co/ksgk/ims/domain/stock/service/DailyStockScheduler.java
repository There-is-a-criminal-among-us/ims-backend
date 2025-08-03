package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.delivery.repository.DeliveryRepository;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceProductRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionStatus;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.domain.stock.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyStockScheduler {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final DeliveryRepository deliveryRepository;
    private final InvoiceProductRepository invoiceProductRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void createDailyStockData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("DailyStock 데이터 생성 시작: {}", yesterday);

        try {
            List<Product> allProducts = productRepository.findAll();
            log.info("처리할 제품 수: {}", allProducts.size());

            for (Product product : allProducts) {
                // 해당 날짜에 이미 DailyStock이 있는지 확인
                boolean exists = stockRepository.existsByProductAndStockDate(product, yesterday);
                if (exists) {
                    log.debug("이미 존재함 - Product: {}, Date: {}", product.getId(), yesterday);
                    continue;
                }

                DailyStock dailyStock = createDailyStockForProduct(product, yesterday);
                stockRepository.save(dailyStock);
                log.debug("DailyStock 생성 완료 - Product: {}, Date: {}", product.getId(), yesterday);
            }

            log.info("DailyStock 데이터 생성 완료: {}", yesterday);
        } catch (Exception e) {
            log.error("DailyStock 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    private DailyStock createDailyStockForProduct(Product product, LocalDate targetDate) {
        // 전날 DailyStock에서 현재 재고 가져오기
        Integer currentStock = getCurrentStock(product, targetDate);

        // 해당 날짜의 Transaction 데이터 집계
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByProductAndCreatedAtBetweenAndTransactionStatus(
                product, startOfDay, endOfDay, TransactionStatus.CONFIRMED);

        // 해당 날짜의 Delivery 데이터 집계
        List<Delivery> deliveries = deliveryRepository.findByRawProductProductMappingsProductAndCreatedAtBetween(
                product, startOfDay, endOfDay);

        // 해당 날짜의 InvoiceProduct 데이터 집계
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findByProductAndInvoiceCreatedAtBetween(
                product, startOfDay, endOfDay);

        return DailyStock.builder()
                .product(product)
                .currentStock(currentStock)
                .incoming(calculateIncoming(transactions))
                .returnIncoming(calculateReturnIncoming(transactions, invoiceProducts))
                .outgoing(calculateOutgoing(transactions))
                .coupangFulfillment(calculateCoupangFulfillment(transactions))
                .naverFulfillment(calculateNaverFulfillment(transactions))
                .deliveryOutgoing(calculateDeliveryOutgoing(deliveries))
                .redelivery(calculateRedelivery(transactions))
                .damaged(calculateDamaged(transactions))
                .disposal(calculateDisposal(transactions))
                .lost(calculateLost(transactions))
                .adjustment(calculateAdjustment(transactions))
                .stockDate(targetDate)
                .build();
    }

    private Integer getCurrentStock(Product product, LocalDate targetDate) {
        // 전날의 DailyStock에서 현재 재고를 가져오기
        LocalDate previousDate = targetDate.minusDays(1);
        // 전날 재고 조회 (없으면 0)
        int previousStock = stockRepository.findByProductAndStockDate(product, previousDate)
                .map(DailyStock::getCurrentStock)
                .orElse(0);
        // 당일 입출고 및 조정 조회 (없으면 0)
        DailyStock dailyStock = stockRepository.findByProductAndStockDate(product, targetDate)
                .orElse(new DailyStock(product, previousStock, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, targetDate));
        int inbound = dailyStock.getInboundTotal();
        int outbound = dailyStock.getOutboundTotal();
        int adjustment = dailyStock.getAdjustmentTotal();
        return previousStock + inbound - outbound - adjustment;
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

    private Integer calculateDeliveryOutgoing(List<Delivery> deliveries) {
        return deliveries.stream()
                .mapToInt(Delivery::getQuantity)
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
}