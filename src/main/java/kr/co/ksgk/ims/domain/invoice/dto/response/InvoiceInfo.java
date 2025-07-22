package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;

import java.time.LocalDateTime;

public record InvoiceInfo(
        Long invoiceId,
        String number,
        String name,
        String phone,
        LocalDateTime createdAt) {

    public static InvoiceInfo from(Invoice invoice) {
        return new InvoiceInfo(
                invoice.getId(),
                invoice.getNumber(),
                invoice.getName(),
                invoice.getPhone(),
                invoice.getCreatedAt()
        );
    }
}
