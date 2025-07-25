package kr.co.ksgk.ims.domain.invoice.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class InvoiceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer returnedQuantity;

    @Column(nullable = false)
    private Integer resalableQuantity;

    @Lob
    private String note;

    @Builder
    public InvoiceProduct(Invoice invoice, Product product, Integer returnedQuantity, Integer resalableQuantity, String note) {
        this.invoice = invoice;
        this.product = product;
        this.returnedQuantity = returnedQuantity;
        this.resalableQuantity = resalableQuantity;
        this.note = note;
    }
}
