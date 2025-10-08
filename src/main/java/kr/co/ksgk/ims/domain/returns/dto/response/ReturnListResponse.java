package kr.co.ksgk.ims.domain.returns.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReturnListResponse(
        long id,
        Long invoiceId,
        LocalDateTime createdAt,
        String handler,
        String mall,
        String buyer,
        String address,
        String phone,
        String productName,
        Integer quantity,
        String originalInvoice,
        ReturnStatus returnStatus
) {
    public static ReturnListResponse from(ReturnInfo returnInfo, InvoiceRepository invoiceRepository) {
        Long invoiceId = null;
        if (returnInfo.getReturnInvoice() != null) {
            invoiceId = invoiceRepository.findByNumber(returnInfo.getReturnInvoice())
                    .map(Invoice::getId)
                    .orElse(null);
        }

        return ReturnListResponse.builder()
                .id(returnInfo.getId())
                .invoiceId(invoiceId)
                .createdAt(returnInfo.getCreatedAt())
                .handler(returnInfo.getReturnHandler().getName())
                .mall(returnInfo.getReturnMall().getName())
                .buyer(returnInfo.getBuyer())
                .address(returnInfo.getAddress())
                .phone(returnInfo.getPhone())
                .productName(returnInfo.getProductName())
                .quantity(returnInfo.getQuantity())
                .originalInvoice(returnInfo.getOriginalInvoice())
                .returnStatus(returnInfo.getReturnStatus())
                .build();
    }
}
