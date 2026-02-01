package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_stock_lot_product_inbound", columnList = "product_id, inbound_date"),
        @Index(name = "idx_stock_lot_remaining", columnList = "product_id, remaining_quantity")
})
public class StockLot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false)
    private LocalDate inboundDate;

    @Column(nullable = false)
    private Integer initialQuantity;

    @Column(nullable = false)
    private Integer remainingQuantity;

    private String lotNumber;

    @Column(nullable = false)
    private Integer freePeriodDays = 0;

    @Builder
    public StockLot(Product product, Transaction transaction, LocalDate inboundDate,
                    Integer initialQuantity, Integer remainingQuantity, String lotNumber,
                    Integer freePeriodDays) {
        this.product = product;
        this.transaction = transaction;
        this.inboundDate = inboundDate;
        this.initialQuantity = initialQuantity;
        this.remainingQuantity = remainingQuantity;
        this.lotNumber = lotNumber;
        this.freePeriodDays = freePeriodDays != null ? freePeriodDays : 0;
    }

    public static StockLot create(Product product, Transaction transaction, LocalDate inboundDate,
                                    Integer quantity, Integer freePeriodDays) {
        return StockLot.builder()
                .product(product)
                .transaction(transaction)
                .inboundDate(inboundDate)
                .initialQuantity(quantity)
                .remainingQuantity(quantity)
                .freePeriodDays(freePeriodDays)
                .build();
    }

    /**
     * FIFO 출고 시 잔여 수량 차감
     * @param quantityToDeduct 차감할 수량
     * @return 실제 차감된 수량
     */
    public int deduct(int quantityToDeduct) {
        if (quantityToDeduct <= 0) {
            return 0;
        }
        int actualDeduction = Math.min(this.remainingQuantity, quantityToDeduct);
        this.remainingQuantity -= actualDeduction;
        return actualDeduction;
    }

    /**
     * 특정 날짜로부터의 경과 일수 계산
     */
    public int getDaysFromInbound(LocalDate targetDate) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(this.inboundDate, targetDate);
    }
}
