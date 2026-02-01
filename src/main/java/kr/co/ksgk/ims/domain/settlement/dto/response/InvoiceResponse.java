package kr.co.ksgk.ims.domain.settlement.dto.response;

import java.util.List;
import java.util.Map;

public record InvoiceResponse(
        Integer year,
        Integer month,
        List<CompanyInvoice> companies
) {
    public record CompanyInvoice(
            Long id,
            String name,
            List<BrandInvoice> brands,
            Map<String, Long> categoryTotal,
            Long total
    ) {}

    public record BrandInvoice(
            Long id,
            String name,
            List<ProductInvoice> products
    ) {}

    public record ProductInvoice(
            Long id,
            String name,
            Map<String, Long> categories
    ) {}
}
