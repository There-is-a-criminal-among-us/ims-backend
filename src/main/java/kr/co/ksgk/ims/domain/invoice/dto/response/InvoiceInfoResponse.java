package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record InvoiceInfoResponse(
        Long invoiceId,
        String name,
        String phone,
        String number,
        String invoiceKeyName,
        String productKeyName,
        LocalDateTime createdAt,
        List<InvoiceProductInfoResponse> products
) {
    public static InvoiceInfoResponse from(Invoice invoice) {
        return InvoiceInfoResponse.builder()
                .invoiceId(invoice.getId())
                .name(invoice.getName())
                .phone(invoice.getPhone())
                .number(invoice.getNumber())
                .invoiceKeyName(invoice.getInvoiceKeyName())
                .productKeyName(invoice.getProductKeyName())
                .createdAt(invoice.getCreatedAt())
                .products(invoice.getInvoiceProducts().stream()
                        .map(InvoiceProductInfoResponse::from)
                        .collect(Collectors.toList())
                )
                .build();
    }
}