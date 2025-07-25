package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import lombok.Builder;

@Builder
public record InvoiceProductInfoResponse(
        Long invoiceProductId,
        Long productId,
        String productImageUrl,
        Integer returnedQuantity,
        Integer resalableQuantity,
        String note
) {
    public static InvoiceProductInfoResponse from(InvoiceProduct invoiceProduct) {
        return InvoiceProductInfoResponse.builder()
                .invoiceProductId(invoiceProduct.getId())
                .productId(invoiceProduct.getProduct().getId())
                .productImageUrl(invoiceProduct.getInvoice().getProductUrl())
                .returnedQuantity(invoiceProduct.getReturnedQuantity())
                .resalableQuantity(invoiceProduct.getResalableQuantity())
                .note(invoiceProduct.getNote())
                .build();
    }
}