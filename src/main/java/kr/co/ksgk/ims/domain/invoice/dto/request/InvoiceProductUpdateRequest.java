package kr.co.ksgk.ims.domain.invoice.dto.request;

public record InvoiceProductUpdateRequest(
        Long productId,
        Integer returnedQuantity,
        Integer resalableQuantity,
        String note
) {
}