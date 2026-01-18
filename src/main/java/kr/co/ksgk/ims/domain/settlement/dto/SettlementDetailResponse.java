package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementDetail;

public record SettlementDetailResponse(
        Long id,
        Long productId,
        String productName,
        Long settlementItemId,
        String settlementItemName,
        CalculationType calculationType,
        Integer quantity,
        Integer unitPrice,
        Integer amount,
        String note
) {
    public static SettlementDetailResponse from(SettlementDetail detail) {
        return new SettlementDetailResponse(
                detail.getId(),
                detail.getProduct().getId(),
                detail.getProduct().getName(),
                detail.getSettlementItem().getId(),
                detail.getSettlementItem().getName(),
                detail.getSettlementItem().getCalculationType(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getAmount(),
                detail.getNote()
        );
    }
}
