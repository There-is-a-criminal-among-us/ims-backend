package kr.co.ksgk.ims.domain.product.dto.response;


import lombok.Builder;

@Builder
public record ProductStatusResponse(
        long productId,
        int currentStock,
        int totalIncoming,
        int totalOutgoing,
        int returnedQuantity,
        int resalableQuantity,
        int totalAdjustment
) {
}