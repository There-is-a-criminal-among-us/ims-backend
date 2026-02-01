package kr.co.ksgk.ims.domain.stock.dto;

import kr.co.ksgk.ims.domain.stock.entity.TransactionWork;
import lombok.Builder;

@Builder
public record TransactionWorkDto(
        Long id,
        Long settlementItemId,
        String settlementItemName,
        Long settlementUnitId,
        String settlementUnitName,
        Integer unitPrice,
        Integer quantity,
        Integer cost
) {
    public static TransactionWorkDto from(TransactionWork work) {
        return TransactionWorkDto.builder()
                .id(work.getId())
                .settlementItemId(work.getSettlementItem().getId())
                .settlementItemName(work.getSettlementItem().getName())
                .settlementUnitId(work.getSettlementUnit() != null ? work.getSettlementUnit().getId() : null)
                .settlementUnitName(work.getSettlementUnit() != null ? work.getSettlementUnit().getName() : null)
                .unitPrice(work.getUnitPrice())
                .quantity(work.getQuantity())
                .cost(work.getTotalCost())
                .build();
    }
}