package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_item_id", nullable = false)
    private SettlementItem settlementItem;

    private Integer quantity;

    private Integer unitPrice;

    private Long amount;

    @Lob
    private String note;

    public static SettlementDetail create(Settlement settlement, Product product, SettlementItem settlementItem,
                                          Integer quantity, Integer unitPrice, Long amount, String note) {
        SettlementDetail detail = new SettlementDetail();
        detail.settlement = settlement;
        detail.product = product;
        detail.settlementItem = settlementItem;
        detail.quantity = quantity;
        detail.unitPrice = unitPrice;
        detail.amount = amount;
        detail.note = note;
        return detail;
    }

    public void update(Integer quantity, Integer unitPrice, Long amount, String note) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.note = note;
    }

    public void updateNote(String note) {
        this.note = note;
    }

    void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
}
