package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.StockLot;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.repository.StockLotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockLotService {

    private final StockLotRepository stockLotRepository;

    /**
     * 입고 시 로트 생성
     */
    @Transactional
    public StockLot createLot(Product product, Transaction transaction, LocalDate inboundDate, Integer quantity) {
        StockLot stockLot = StockLot.create(product, transaction, inboundDate, quantity);
        StockLot savedLot = stockLotRepository.save(stockLot);
        log.info("StockLot 생성 - Product: {}, Date: {}, Quantity: {}",
                product.getId(), inboundDate, quantity);
        return savedLot;
    }

    /**
     * 입고 시 로트 생성 (로트 번호 포함)
     */
    @Transactional
    public StockLot createLotWithLotNumber(Product product, Transaction transaction,
                                            LocalDate inboundDate, Integer quantity, String lotNumber) {
        StockLot stockLot = StockLot.createWithLotNumber(product, transaction, inboundDate, quantity, lotNumber);
        StockLot savedLot = stockLotRepository.save(stockLot);
        log.info("StockLot 생성 (lotNumber: {}) - Product: {}, Date: {}, Quantity: {}",
                lotNumber, product.getId(), inboundDate, quantity);
        return savedLot;
    }

    /**
     * FIFO 방식 출고 처리
     * @return 실제 차감된 총 수량
     */
    @Transactional
    public int deductFifo(Product product, int quantityToDeduct) {
        if (quantityToDeduct <= 0) {
            return 0;
        }

        List<StockLot> lots = stockLotRepository.findByProductWithRemainingOrderByInboundDateAsc(product);

        int remainingToDeduct = quantityToDeduct;
        int totalDeducted = 0;

        for (StockLot lot : lots) {
            if (remainingToDeduct <= 0) {
                break;
            }

            int deducted = lot.deduct(remainingToDeduct);
            remainingToDeduct -= deducted;
            totalDeducted += deducted;

            log.debug("FIFO 차감 - Lot: {}, InboundDate: {}, Deducted: {}, Remaining: {}",
                    lot.getId(), lot.getInboundDate(), deducted, lot.getRemainingQuantity());
        }

        if (remainingToDeduct > 0) {
            log.warn("FIFO 차감 부족 - Product: {}, Requested: {}, Deducted: {}, Short: {}",
                    product.getId(), quantityToDeduct, totalDeducted, remainingToDeduct);
        }

        log.info("FIFO 출고 완료 - Product: {}, Total Deducted: {}", product.getId(), totalDeducted);
        return totalDeducted;
    }

    /**
     * 특정 상품의 로트 현황 조회
     */
    public List<StockLot> getLotsByProduct(Product product) {
        return stockLotRepository.findByProductOrderByInboundDateDesc(product);
    }

    /**
     * 특정 상품의 잔여 수량이 있는 로트 조회
     */
    public List<StockLot> getLotsWithRemainingByProduct(Product product) {
        return stockLotRepository.findByProductWithRemainingOrderByInboundDateAsc(product);
    }

    /**
     * 특정 로트 조회
     */
    public StockLot getLotById(Long lotId) {
        return stockLotRepository.findById(lotId).orElse(null);
    }

    /**
     * 특정 상품의 총 잔여 수량
     */
    public int getTotalRemainingByProduct(Product product) {
        return stockLotRepository.getTotalRemainingByProduct(product);
    }

    /**
     * 잔여 수량이 있는 모든 로트 조회 (DailyStockLot 생성용)
     */
    public List<StockLot> getAllLotsWithRemaining() {
        return stockLotRepository.findAllWithRemaining();
    }
}
