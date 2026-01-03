package kr.co.ksgk.ims.domain.returns.dto.response;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.returns.entity.OrderType;
import kr.co.ksgk.ims.domain.returns.entity.ProcessingStatus;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ReturnResponse(
        long id,
        Long invoiceId,
        LocalDateTime createdAt,
        long handlerId,
        String handler,
        long mallId,
        String mall,
        String buyer,
        String receiver,
        String address,
        String phone,
        String productName,
        int quantity,
        String originalInvoice,
        LocalDate acceptDate,
        ReturnStatus returnStatus,
        String returnInvoice,
        String note,
        OrderType orderType,
        ProcessingStatus processingStatus
) {
    public static ReturnResponse from(ReturnInfo returnInfo, InvoiceRepository invoiceRepository) {
        Long invoiceId = null;
        if (returnInfo.getReturnInvoice() != null) {
            invoiceId = invoiceRepository.findByNumber(returnInfo.getReturnInvoice())
                    .map(Invoice::getId)
                    .orElse(null);
        }

        return ReturnResponse.builder()
                .id(returnInfo.getId())
                .invoiceId(invoiceId)
                .createdAt(returnInfo.getCreatedAt())
                .handlerId(returnInfo.getReturnHandler().getId())
                .handler(returnInfo.getReturnHandler().getName())
                .mallId(returnInfo.getReturnMall().getId())
                .mall(returnInfo.getReturnMall().getName())
                .buyer(returnInfo.getBuyer())
                .receiver(returnInfo.getReceiver())
                .address(returnInfo.getAddress())
                .phone(returnInfo.getPhone())
                .productName(returnInfo.getProductName())
                .quantity(returnInfo.getQuantity())
                .originalInvoice(returnInfo.getOriginalInvoice())
                .acceptDate(returnInfo.getAcceptDate())
                .returnStatus(returnInfo.getReturnStatus())
                .returnInvoice(returnInfo.getReturnInvoice())
                .note(returnInfo.getNote())
                .orderType(returnInfo.getOrderType())
                .processingStatus(returnInfo.getProcessingStatus())
                .build();
    }
}
