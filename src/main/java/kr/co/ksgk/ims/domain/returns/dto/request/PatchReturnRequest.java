package kr.co.ksgk.ims.domain.returns.dto.request;

import kr.co.ksgk.ims.domain.returns.entity.OrderType;
import kr.co.ksgk.ims.domain.returns.entity.ProcessingStatus;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;

import java.time.LocalDate;

public record PatchReturnRequest(
        String buyer,
        String receiver,
        String address,
        String phone,
        String productName,
        Integer quantity,
        String originalInvoice,
        LocalDate acceptDate,
        ReturnStatus returnStatus,
        String returnInvoice,
        String note,
        Long handlerId,
        Long mallId,
        OrderType orderType,
        ProcessingStatus processingStatus
) {
}