package kr.co.ksgk.ims.domain.product.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RawProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_unit_id")
    private SettlementUnit sizeUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_size_unit_id")
    private SettlementUnit returnSizeUnit;

    private String coupangCode;

    @Builder
    public RawProduct(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "rawProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductMapping> productMappings = new ArrayList<>();

    @OneToMany(mappedBy = "rawProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Delivery> deliveries = new ArrayList<>();

    public void updateSizeUnit(SettlementUnit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public void updateReturnSizeUnit(SettlementUnit returnSizeUnit) {
        this.returnSizeUnit = returnSizeUnit;
    }

    public void updateCoupangCode(String coupangCode) {
        this.coupangCode = coupangCode;
    }
}
