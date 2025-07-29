package kr.co.ksgk.ims.domain.invoice.dto.request;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;

import java.util.List;

public record UploadInvoiceInfoRequest(
        Long companyId,
        String name,
        String phone,
        String number,
        String invoiceImageUrl,
        String productKeyName,
        List<SimpleProductInfo> products
) {
    public Invoice toEntity(Company company, String productImageUrl) {
        return Invoice.builder()
                .company(company)
                .name(name)
                .phone(phone)
                .number(number)
                .invoiceUrl(invoiceImageUrl)
                .productUrl(productImageUrl)
                .build();
    }
}