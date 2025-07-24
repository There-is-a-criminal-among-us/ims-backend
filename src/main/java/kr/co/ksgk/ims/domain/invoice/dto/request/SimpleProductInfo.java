package kr.co.ksgk.ims.domain.invoice.dto.request;

public record SimpleProductInfo(
        Long productId,
        Integer returnedQuantity,
        Integer resaleableQuantity,
        String note
) {
}
