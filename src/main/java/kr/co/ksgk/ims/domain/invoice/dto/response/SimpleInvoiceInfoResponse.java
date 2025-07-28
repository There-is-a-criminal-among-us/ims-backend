package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SimpleInvoiceInfoResponse(
        long id,
        String number,
        String name,
        String phone,
        LocalDateTime createdAt
) {
    public static SimpleInvoiceInfoResponse from(Invoice invoice) {
        return SimpleInvoiceInfoResponse.builder()
                .id(invoice.getId())
                .number(invoice.getNumber())
                .name(invoice.getName())
                .phone(invoice.getPhone())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}