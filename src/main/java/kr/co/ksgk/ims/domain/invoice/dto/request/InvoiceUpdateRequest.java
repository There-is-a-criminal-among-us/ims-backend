package kr.co.ksgk.ims.domain.invoice.dto.request;

import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceProductInfoResponse;

import java.util.List;

public record InvoiceUpdateRequest(
        String name,
        String phone,
        String invoiceUrl,
        List<InvoiceProductInfoResponse> products
) {
}
