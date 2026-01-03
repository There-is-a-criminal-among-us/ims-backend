package kr.co.ksgk.ims.domain.invoice.dto.response;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record InvoiceInfoResponse(
        Long invoiceId,
        String name,
        String phone,
        String number,
        String invoiceUrl,
        String productUrl,
        LocalDateTime createdAt,
        List<InvoiceProductInfoResponse> products
) {
    public static InvoiceInfoResponse from(Invoice invoice, S3Service s3Service) {
        return InvoiceInfoResponse.builder()
                .invoiceId(invoice.getId())
                .name(invoice.getName())
                .phone(invoice.getPhone())
                .number(invoice.getNumber())
                .invoiceUrl(s3Service.generateStaticUrl(invoice.getInvoiceKeyName()))
                .productUrl(s3Service.generateStaticUrl(invoice.getProductKeyName()))
                .createdAt(invoice.getCreatedAt())
                .products(invoice.getInvoiceProducts().stream()
                        .map(InvoiceProductInfoResponse::from)
                        .collect(Collectors.toList())
                )
                .build();
    }
}