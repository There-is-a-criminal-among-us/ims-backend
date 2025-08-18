package kr.co.ksgk.ims.domain.product.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
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

    @Builder
    public RawProduct(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "rawProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductMapping> productMappings = new ArrayList<>();

    @OneToMany(mappedBy = "rawProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Delivery> deliveries = new ArrayList<>();
}
