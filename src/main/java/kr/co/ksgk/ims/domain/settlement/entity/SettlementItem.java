package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalculationType calculationType = CalculationType.MANUAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private SettlementType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private SettlementCategory category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementUnit> units = new ArrayList<>();

    public static SettlementItem createForType(String name, int displayOrder, CalculationType calculationType, SettlementType type) {
        SettlementItem item = new SettlementItem();
        item.name = name;
        item.displayOrder = displayOrder;
        item.calculationType = calculationType;
        item.type = type;
        item.category = null;
        return item;
    }

    public static SettlementItem createForCategory(String name, int displayOrder, CalculationType calculationType, SettlementCategory category) {
        SettlementItem item = new SettlementItem();
        item.name = name;
        item.displayOrder = displayOrder;
        item.calculationType = calculationType;
        item.type = null;
        item.category = category;
        return item;
    }

    public void update(String name, int displayOrder, CalculationType calculationType) {
        this.name = name;
        this.displayOrder = displayOrder;
        this.calculationType = calculationType;
    }

    public void updateUnits(List<SettlementUnit> units) {
        this.units.clear();
        this.units.addAll(units);
    }
}
