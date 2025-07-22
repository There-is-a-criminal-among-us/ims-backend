package kr.co.ksgk.ims.domain.invoice.dto.request;

public record ProductInfo(
        Long productId,
        Integer returnedQuantity,
        Integer resaleableQuantity,
        String note
) {
}
