package kr.co.ksgk.ims.domain.stock.dto.response;

import kr.co.ksgk.ims.domain.stock.entity.StockLot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockLotResponse(
        Long id,
        Long productId,
        String productName,
        Long transactionId,
        LocalDate inboundDate,
        Integer initialQuantity,
        Integer remainingQuantity,
        String lotNumber,
        Integer freePeriodDays,
        LocalDateTime createdAt
) {
    public static StockLotResponse from(StockLot stockLot) {
        return new StockLotResponse(
                stockLot.getId(),
                stockLot.getProduct().getId(),
                stockLot.getProduct().getName(),
                stockLot.getTransaction() != null ? stockLot.getTransaction().getId() : null,
                stockLot.getInboundDate(),
                stockLot.getInitialQuantity(),
                stockLot.getRemainingQuantity(),
                stockLot.getLotNumber(),
                stockLot.getFreePeriodDays(),
                stockLot.getCreatedAt()
        );
    }
}
