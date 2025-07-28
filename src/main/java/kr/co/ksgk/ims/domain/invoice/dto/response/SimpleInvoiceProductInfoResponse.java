package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SimpleInvoiceProductInfoResponse(
        long id,
        String name,
        String number,
        String productName,
        Integer returnedQuantity,
        Integer resalableQuantity,
        LocalDateTime createdAt
) {
    public static SimpleInvoiceProductInfoResponse from(InvoiceProduct invoiceProduct) {
        return SimpleInvoiceProductInfoResponse.builder()
                .id(invoiceProduct.getInvoice().getId())
                .name(invoiceProduct.getInvoice().getName())
                .number(invoiceProduct.getInvoice().getNumber())
                .productName(invoiceProduct.getProduct().getName())
                .returnedQuantity(invoiceProduct.getReturnedQuantity())
                .resalableQuantity(invoiceProduct.getResalableQuantity())
                .createdAt(invoiceProduct.getInvoice().getCreatedAt())
                .build();
    }
}