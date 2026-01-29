package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliverySheetRow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @Column(columnDefinition = "JSON")
    private String remoteAreaFees;

    @Column(nullable = false)
    private Boolean costTarget = true;

    @Column(nullable = false)
    private Integer quantity = 1;

    public static DeliverySheetRow create(Integer year, Integer month, String productName,
                                          Product product, WorkType workType, String remoteAreaFees,
                                          Boolean costTarget, Integer quantity) {
        DeliverySheetRow row = new DeliverySheetRow();
        row.year = year;
        row.month = month;
        row.productName = productName;
        row.product = product;
        row.workType = workType;
        row.remoteAreaFees = remoteAreaFees;
        row.costTarget = costTarget;
        row.quantity = quantity;
        return row;
    }
}
