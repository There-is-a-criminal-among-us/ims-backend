package kr.co.ksgk.ims.domain.stock.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDate;

@RedisHash("daily_stock")
@Getter
@NoArgsConstructor
public class DailyStockCache {

    @Id
    private String id; // daily_stock:{product_id}:{date}

    private Long productId;
    private LocalDate stockDate;
    private Integer currentStock;
    private Integer incoming;
    private Integer returnIncoming;
    private Integer outgoing;
    private Integer coupangFulfillment;
    private Integer naverFulfillment;
    private Integer deliveryOutgoing;
    private Integer redelivery;
    private Integer damaged;
    private Integer disposal;
    private Integer lost;
    private Integer adjustment;

    @TimeToLive
    private Long ttl; // TTL in seconds

    @Builder
    public DailyStockCache(Long productId, LocalDate stockDate, Integer currentStock, 
                          Integer incoming, Integer returnIncoming, Integer outgoing,
                          Integer coupangFulfillment, Integer naverFulfillment, 
                          Integer deliveryOutgoing, Integer redelivery, Integer damaged,
                          Integer disposal, Integer lost, Integer adjustment, Long ttl) {
        this.id = "daily_stock:" + productId + ":" + stockDate;
        this.productId = productId;
        this.stockDate = stockDate;
        this.currentStock = currentStock;
        this.incoming = incoming;
        this.returnIncoming = returnIncoming;
        this.outgoing = outgoing;
        this.coupangFulfillment = coupangFulfillment;
        this.naverFulfillment = naverFulfillment;
        this.deliveryOutgoing = deliveryOutgoing;
        this.redelivery = redelivery;
        this.damaged = damaged;
        this.disposal = disposal;
        this.lost = lost;
        this.adjustment = adjustment;
        this.ttl = ttl;
    }

    public static DailyStockCache fromDailyStock(DailyStock dailyStock, Long ttl) {
        return DailyStockCache.builder()
                .productId(dailyStock.getProduct().getId())
                .stockDate(dailyStock.getStockDate())
                .currentStock(dailyStock.getCurrentStock())
                .incoming(dailyStock.getIncoming())
                .returnIncoming(dailyStock.getReturnIncoming())
                .outgoing(dailyStock.getOutgoing())
                .coupangFulfillment(dailyStock.getCoupangFulfillment())
                .naverFulfillment(dailyStock.getNaverFulfillment())
                .deliveryOutgoing(dailyStock.getDeliveryOutgoing())
                .redelivery(dailyStock.getRedelivery())
                .damaged(dailyStock.getDamaged())
                .disposal(dailyStock.getDisposal())
                .lost(dailyStock.getLost())
                .adjustment(dailyStock.getAdjustment())
                .ttl(ttl)
                .build();
    }

    public int getInboundTotal() {
        return incoming + returnIncoming;
    }

    public int getOutboundTotal() {
        return outgoing + coupangFulfillment + naverFulfillment + deliveryOutgoing;
    }

    public int getAdjustmentTotal() {
        return damaged + disposal + lost + redelivery - adjustment;
    }
}