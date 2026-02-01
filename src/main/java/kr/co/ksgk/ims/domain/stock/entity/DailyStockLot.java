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
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_stock_lot_date",
                columnNames = {"stock_lot_id", "stock_date"}
        ),
        indexes = {
                @Index(name = "idx_daily_stock_lot_product_date", columnList = "product_id, stock_date"),
                @Index(name = "idx_daily_stock_lot_date", columnList = "stock_date"),
                @Index(name = "idx_daily_stock_lot_inbound", columnList = "product_id, inbound_date")
        }
)
public class DailyStockLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_lot_id", nullable = false)
    private StockLot stockLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDate stockDate;

    @Column(nullable = false)
    private Integer quantity;

    // 역정규화 (조회 성능 최적화)
    @Column(nullable = false)
    private LocalDate inboundDate;

    @Column(nullable = false)
    private Integer daysFromInbound;

    @Builder
    public DailyStockLot(StockLot stockLot, Product product, LocalDate stockDate,
                         Integer quantity, LocalDate inboundDate, Integer daysFromInbound) {
        this.stockLot = stockLot;
        this.product = product;
        this.stockDate = stockDate;
        this.quantity = quantity;
        this.inboundDate = inboundDate;
        this.daysFromInbound = daysFromInbound;
    }

    public static DailyStockLot create(StockLot stockLot, LocalDate stockDate) {
        int daysFromInbound = stockLot.getDaysFromInbound(stockDate);
        return DailyStockLot.builder()
                .stockLot(stockLot)
                .product(stockLot.getProduct())
                .stockDate(stockDate)
                .quantity(stockLot.getRemainingQuantity())
                .inboundDate(stockLot.getInboundDate())
                .daysFromInbound(daysFromInbound)
                .build();
    }

    /**
     * 무료 기간 내인지 확인
     */
    public boolean isWithinFreePeriod(int freePeriodDays) {
        return this.daysFromInbound <= freePeriodDays;
    }

    /**
     * 보관료 과금 대상 수량 반환
     * 무료 기간 내면 0, 초과하면 전체 수량 반환
     */
    public int getBillableQuantity(int freePeriodDays) {
        return isWithinFreePeriod(freePeriodDays) ? 0 : this.quantity;
    }
}
