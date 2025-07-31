package kr.co.ksgk.ims.domain.invoice.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Invoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 40, nullable = false)
    private String phone;

    @Column(length = 20, nullable = false)
    private String number;

    @Column(length = 512, nullable = false)
    private String invoiceKeyName;

    @Column(length = 512, nullable = false)
    private String productKeyName;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceProduct> invoiceProducts = new ArrayList<>();

    @Builder
    public Invoice(String name, String phone, String number, String invoiceKeyName, String productKeyName) {
        this.name = name;
        this.phone = phone;
        this.number = number;
        this.invoiceKeyName = invoiceKeyName;
        this.productKeyName = productKeyName;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateInvoiceKeyName(String invoiceKeyName) {
        this.invoiceKeyName = invoiceKeyName;
    }

    public void updateProductKeyName(String productKeyName) {
        this.productKeyName = productKeyName;
    }

    public void updateInvoiceProduct(List<InvoiceProduct> invoiceProducts) {
        this.invoiceProducts.clear();
        this.invoiceProducts.addAll(invoiceProducts);
    }
}