package kr.co.ksgk.ims.domain.product.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandId", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Lob
    private String note;

    private LocalDateTime deletedAt;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceProduct> invoiceProducts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItemMapping> deliveryItemMappings = new ArrayList<>();

    @Builder
    public Product(Brand brand, String name, String note) {
        this.brand = brand;
        this.name = name;
        this.note = note;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateNote(String note) {
        this.note = note;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }


}
