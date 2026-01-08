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
public class SettlementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementItem> directItems = new ArrayList<>();

    public static SettlementType create(String name, int displayOrder) {
        SettlementType type = new SettlementType();
        type.name = name;
        type.displayOrder = displayOrder;
        return type;
    }

    public void update(String name, int displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public void updateCategories(List<SettlementCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }

    public void updateDirectItems(List<SettlementItem> directItems) {
        this.directItems.clear();
        this.directItems.addAll(directItems);
    }
}
