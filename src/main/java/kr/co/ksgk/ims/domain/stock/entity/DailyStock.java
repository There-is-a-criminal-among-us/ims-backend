package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer incoming;

    @Column(nullable = false)
    private Integer returnIncoming;

    @Column(nullable = false)
    private Integer outgoing;

    @Column(nullable = false)
    private Integer coupangFulfillment;

    @Column(nullable = false)
    private Integer naverFulfillment;

    @Column(nullable = false)
    private Integer deliveryOutgoing;

    @Column(nullable = false)
    private Integer redelivery;

    @Column(nullable = false)
    private Integer damaged;

    @Column(nullable = false)
    private Integer disposal;

    @Column(nullable = false)
    private Integer lost;

    @Column(nullable = false)
    private Integer adjustment;

    @Column(nullable = false)
    private LocalDate stockDate;

    @Builder
    public DailyStock(Product product, Integer currentStock, Integer incoming, Integer returnIncoming,
                     Integer outgoing, Integer coupangFulfillment, Integer naverFulfillment,
                     Integer deliveryOutgoing, Integer redelivery, Integer damaged, Integer disposal,
                     Integer lost, Integer adjustment, LocalDate stockDate) {
        this.product = product;
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
        this.stockDate = stockDate;
    }

    public int getInboundTotal() {
        return incoming + returnIncoming;
    }

    public int getOutboundTotal() {
        return outgoing + coupangFulfillment + naverFulfillment + deliveryOutgoing;
    }

    public int getAdjustmentTotal() {
        return damaged + disposal + lost + adjustment + redelivery;
    }
}
