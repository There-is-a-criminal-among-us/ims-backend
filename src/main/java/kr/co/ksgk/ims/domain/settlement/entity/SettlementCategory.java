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
public class SettlementCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private SettlementType type;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementItem> items = new ArrayList<>();

    public static SettlementCategory create(String name, int displayOrder, SettlementType type) {
        SettlementCategory category = new SettlementCategory();
        category.name = name;
        category.displayOrder = displayOrder;
        category.type = type;
        return category;
    }

    public void update(String name, int displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public void updateItems(List<SettlementItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }
}
