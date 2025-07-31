package kr.co.ksgk.ims.domain.invoice.dto.request;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;

import java.util.List;

public record UploadInvoiceInfoRequest(
        String name,
        String phone,
        String number,
        String invoiceKeyName,
        String productKeyName,
        List<SimpleProductInfo> products
) {
    public Invoice toEntity() {
        return Invoice.builder()
                .name(name)
                .phone(phone)
                .number(number)
                .invoiceKeyName(invoiceKeyName)
                .productKeyName(productKeyName)
                .build();
    }
}