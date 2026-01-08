package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SettlementItem item;

    public static SettlementUnit create(String name, int price, int displayOrder, SettlementItem item) {
        SettlementUnit unit = new SettlementUnit();
        unit.name = name;
        unit.price = price;
        unit.displayOrder = displayOrder;
        unit.item = item;
        return unit;
    }

    public void update(String name, int price, int displayOrder) {
        this.name = name;
        this.price = price;
        this.displayOrder = displayOrder;
    }
}
