package kr.co.ksgk.ims.domain.settlement.dto.response;

import kr.co.ksgk.ims.domain.settlement.entity.DeliverySheetReturn;

public record DeliverySheetReturnResponse(
        String pickupDate,
        String invoiceNumber,
        String workType,
        String senderName,
        String receiverName,
        String productName,
        Integer amount
) {
    public static DeliverySheetReturnResponse from(DeliverySheetReturn row) {
        return new DeliverySheetReturnResponse(
                row.getPickupDate(),
                row.getInvoiceNumber(),
                row.getWorkType(),
                row.getSenderName(),
                row.getReceiverName(),
                row.getProductName(),
                row.getAmount()
        );
    }
}
