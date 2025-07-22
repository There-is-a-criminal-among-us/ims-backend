package kr.co.ksgk.ims.domain.invoice.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class InvoiceProduct
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer returnedQuantity;
    private Integer resaleableQuantity;
    private String note;

    public InvoiceProduct(Invoice invoice, Product product, Integer returnedQuantity, Integer resaleableQuantity, String note)
    {
        this.invoice = invoice;
        this.product = product;
        this.returnedQuantity = returnedQuantity;
        this.resaleableQuantity = resaleableQuantity;
        this.note = note;
    }
}
