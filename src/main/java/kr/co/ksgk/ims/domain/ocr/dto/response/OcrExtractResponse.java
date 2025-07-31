package kr.co.ksgk.ims.domain.ocr.dto.response;

import kr.co.ksgk.ims.domain.product.dto.response.ProductMappingResponse;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import lombok.Builder;

import java.util.List;

@Builder
public record OcrExtractResponse(
        String senderName,
        String senderPhone,
        String invoiceNumber,
        ProductMappingResponse products,
        String invoiceImageUrl
) {
    public static OcrExtractResponse of(ExtractedInvoice extractedInvoice, List<ProductMapping> productMappings, String invoiceImageUrl) {
        return OcrExtractResponse.builder()
                .senderName(extractedInvoice.sender_name())
                .senderPhone(extractedInvoice.sender_phone())
                .invoiceNumber(extractedInvoice.invoice_number())
                .products(ProductMappingResponse.from(productMappings))
                .invoiceImageUrl(invoiceImageUrl)
                .build();
    }
}
