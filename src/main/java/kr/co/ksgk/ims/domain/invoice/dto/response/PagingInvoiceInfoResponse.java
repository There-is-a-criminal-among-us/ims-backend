package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingInvoiceInfoResponse(
        PageResponse page,
        List<InvoiceProductInfoResponse> invoiceProducts
) {
    public static PagingInvoiceInfoResponse of(Page<InvoiceProduct> pageInvoiceProduct, List<InvoiceProductInfoResponse> invoiceProductInfoResponses) {
        return PagingInvoiceInfoResponse.builder()
                .page(PageResponse.from(pageInvoiceProduct))
                .invoiceProducts(invoiceProductInfoResponses)
                .build();
    }

}





