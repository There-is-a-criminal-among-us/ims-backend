package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SimpleInvoiceProductInfoResponse(
        Long invoiceId,
        String invoiceName,
        String invoiceNumber,
        String productName,
        Integer returnedQuantity,
        Integer resaleableQuantity,
        LocalDateTime createdAt
) {
    public static SimpleInvoiceProductInfoResponse from(InvoiceProduct invoiceProduct) {
        return SimpleInvoiceProductInfoResponse.builder()
                .invoiceId(invoiceProduct.getInvoice().getId())
                .invoiceName(invoiceProduct.getInvoice().getName())
                .invoiceNumber(invoiceProduct.getInvoice().getNumber())
                .productName(invoiceProduct.getProduct().getName())
                .returnedQuantity(invoiceProduct.getReturnedQuantity())
                .resaleableQuantity(invoiceProduct.getResalableQuantity())
                .createdAt(invoiceProduct.getInvoice().getCreatedAt())
                .build();
    }
}