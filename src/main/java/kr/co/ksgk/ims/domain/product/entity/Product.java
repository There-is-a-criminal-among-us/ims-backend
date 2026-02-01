package kr.co.ksgk.ims.domain.product.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonBackReference
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Lob
    private String note;

    private LocalDateTime deletedAt;

    // 보관료 설정
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    @Column(precision = 10, scale = 4)
    private BigDecimal cbm;

    @Column(precision = 10, scale = 2)
    private BigDecimal storagePricePerCbm;

    private Integer quantityPerPallet;

    @Column(precision = 10, scale = 2)
    private BigDecimal storagePricePerPallet;

    @Builder
    public Product(Brand brand, String name, String note) {
        this.brand = brand;
        this.name = name;
        this.note = note;
    }

    @OneToMany(mappedBy = "product")
    private List<InvoiceProduct> invoiceProducts = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductMapping> productMappings = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Transaction> transactions = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void updateNote(String note) {
        this.note = note;
    }

    public void updateStorageSettings(StorageType storageType, BigDecimal cbm, BigDecimal storagePricePerCbm,
                                       Integer quantityPerPallet, BigDecimal storagePricePerPallet) {
        this.storageType = storageType;
        this.cbm = cbm;
        this.storagePricePerCbm = storagePricePerCbm;
        this.quantityPerPallet = quantityPerPallet;
        this.storagePricePerPallet = storagePricePerPallet;
    }

}
