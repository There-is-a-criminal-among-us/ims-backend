package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_item_id", nullable = false)
    private SettlementItem settlementItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_unit_id")
    private SettlementUnit settlementUnit;

    private Integer quantity;

    private Integer unitPrice;

    private Integer cost;

    public static TransactionWork createWithUnit(Transaction transaction, SettlementItem item, SettlementUnit unit, int quantity) {
        TransactionWork work = new TransactionWork();
        work.transaction = transaction;
        work.settlementItem = item;
        work.settlementUnit = unit;
        work.quantity = quantity;
        work.unitPrice = unit.getPrice();
        work.cost = unit.getPrice() * quantity;
        return work;
    }

    public static TransactionWork createWithoutUnit(Transaction transaction, SettlementItem item, int cost) {
        TransactionWork work = new TransactionWork();
        work.transaction = transaction;
        work.settlementItem = item;
        work.settlementUnit = null;
        work.quantity = 1;
        work.unitPrice = cost;
        work.cost = cost;
        return work;
    }

    public void update(SettlementUnit unit, Integer quantity, Integer cost) {
        this.settlementUnit = unit;
        this.quantity = quantity;
        if (unit != null && quantity != null) {
            this.unitPrice = unit.getPrice();
            this.cost = unit.getPrice() * quantity;
        } else {
            this.quantity = 1;
            this.unitPrice = cost;
            this.cost = cost;
        }
    }

    public int getTotalCost() {
        return cost != null ? cost : 0;
    }
}