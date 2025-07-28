package kr.co.ksgk.ims.domain.invoice.dto.request;

import java.util.List;

public record InvoiceUpdateRequest(
        String name,
        String phone,
        String invoiceUrl,
        List<InvoiceProductUpdateRequest> products
) {
}