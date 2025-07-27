package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "dailyStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public int getInboundTotal() {
        return incoming + returnIncoming;
    }

    public int getOutboundTotal() {
        return outgoing + coupangFulfillment + naverFulfillment + deliveryOutgoing + redelivery;
    }

    public int getAdjustmentTotal() {
        return damaged + disposal + lost + adjustment;
    }
}
