package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InvoiceProductInfoResponse(
        Long invoiceId,
        String invoiceName,
        String invoiceNumber,
        String productName,
        Integer returnedQuantity,
        Integer resaleableQuantity,
        LocalDateTime createdAt
) {
    public static InvoiceProductInfoResponse from(InvoiceProduct invoiceProduct){
        return InvoiceProductInfoResponse.builder()
                .invoiceId(invoiceProduct.getInvoice().getId())
                .invoiceName(invoiceProduct.getInvoice().getName())
                .invoiceNumber(invoiceProduct.getInvoice().getNumber())
                .productName(invoiceProduct.getProduct().getName())
                .returnedQuantity(invoiceProduct.getReturnedQuantity())
                .resaleableQuantity(invoiceProduct.getResaleableQuantity())
                .createdAt(invoiceProduct.getInvoice().getCreatedAt())
                .build();
    }
}
