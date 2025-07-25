package kr.co.ksgk.ims.domain.stock.dto.response;

import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DailyStockDetailsDto(
        long productId,
        LocalDate date,
        int currentStock,
        int inboundTotal,
        InboundDetails inboundDetails,
        int outboundTotal,
        OutboundDetails outboundDetails,
        int adjustmentTotal,
        AdjustmentDetails adjustmentDetails
) {
    public static DailyStockDetailsDto from(DailyStock dailyStock) {
        return DailyStockDetailsDto.builder()
                .productId(dailyStock.getProduct().getId())
                .date(dailyStock.getStockDate())
                .currentStock(dailyStock.getCurrentStock())
                .inboundTotal(dailyStock.getInboundTotal())
                .inboundDetails(InboundDetails.builder()
                        .incoming(dailyStock.getIncoming())
                        .returnIncoming(dailyStock.getReturnIncoming())
                        .build())
                .outboundTotal(dailyStock.getOutboundTotal())
                .outboundDetails(OutboundDetails.builder()
                        .outgoing(dailyStock.getOutgoing())
                        .coupangFulfillment(dailyStock.getCoupangFulfillment())
                        .naverFulfillment(dailyStock.getNaverFulfillment())
                        .deliveryOutgoing(dailyStock.getDeliveryOutgoing())
                        .redelivery(dailyStock.getRedelivery())
                        .build())
                .adjustmentTotal(dailyStock.getAdjustmentTotal())
                .adjustmentDetails(AdjustmentDetails.builder()
                        .damaged(dailyStock.getDamaged())
                        .disposal(dailyStock.getDisposal())
                        .lost(dailyStock.getLost())
                        .adjustment(dailyStock.getAdjustment())
                        .build())
                .build();
    }

    @Builder
    public record InboundDetails(
            int incoming,
            int returnIncoming
    ) {
    }

    @Builder
    public record OutboundDetails(
            int outgoing,
            int coupangFulfillment,
            int naverFulfillment,
            int deliveryOutgoing,
            int redelivery
    ) {
    }

    @Builder
    public record AdjustmentDetails(
            int damaged,
            int disposal,
            int lost,
            int adjustment
    ) {
    }
}