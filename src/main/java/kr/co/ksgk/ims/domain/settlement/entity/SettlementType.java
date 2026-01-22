package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
    private Set<SettlementCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SettlementItem> directItems = new HashSet<>();

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

    public void updateCategories(Set<SettlementCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }

    public void updateDirectItems(Set<SettlementItem> directItems) {
        this.directItems.clear();
        this.directItems.addAll(directItems);
    }
}
