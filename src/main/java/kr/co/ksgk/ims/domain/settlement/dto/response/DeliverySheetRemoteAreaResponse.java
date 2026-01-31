package kr.co.ksgk.ims.domain.settlement.dto.response;

import kr.co.ksgk.ims.domain.settlement.entity.DeliverySheetRemoteArea;

public record DeliverySheetRemoteAreaResponse(
        String pickupDate,
        String invoiceNumber,
        String senderName,
        String receiverName,
        String receiverAddress,
        String productName,
        Integer totalFee
) {
    public static DeliverySheetRemoteAreaResponse from(DeliverySheetRemoteArea row) {
        return new DeliverySheetRemoteAreaResponse(
                row.getPickupDate(),
                row.getInvoiceNumber(),
                row.getSenderName(),
                row.getReceiverName(),
                row.getReceiverAddress(),
                row.getProductName(),
                row.getTotalFee()
        );
    }
}
