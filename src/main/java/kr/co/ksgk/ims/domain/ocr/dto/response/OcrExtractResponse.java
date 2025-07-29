package kr.co.ksgk.ims.domain.ocr.dto.response;

import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record OcrExtractResponse(
        String senderName,
        String senderPhone,
        String invoiceNumber,
        List<ProductResponse> products,
        String invoiceImageUrl
) {
    public static OcrExtractResponse of(ExtractedInvoice extractedInvoice, List<Product> products, String invoiceImageUrl) {
        return OcrExtractResponse.builder()
                .senderName(extractedInvoice.sender_name())
                .senderPhone(extractedInvoice.sender_phone())
                .invoiceNumber(extractedInvoice.invoice_number())
                .products(products.stream()
                        .map(ProductResponse::from)
                        .collect(Collectors.toList()))
                .invoiceImageUrl(invoiceImageUrl)
                .build();
    }
}
