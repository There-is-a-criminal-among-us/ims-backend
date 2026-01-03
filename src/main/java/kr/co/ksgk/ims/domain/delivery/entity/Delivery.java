package kr.co.ksgk.ims.domain.delivery.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_product_id", nullable = false)
    private RawProduct rawProduct;

    @Builder
    public Delivery(Integer quantity, RawProduct rawProduct) {
        this.quantity = quantity;
        this.rawProduct = rawProduct;
    }
}
