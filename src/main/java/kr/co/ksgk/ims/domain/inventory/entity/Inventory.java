package kr.co.ksgk.ims.domain.inventory.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "inventory_status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "inventory_date"})
        }
)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 3, nullable = false)
    private String inventoryDate;

    @Column
    private Integer stock;

    @Column
    private Integer outgoing;

    @Column
    private Integer fulfillment;

    @Column
    private Integer incoming;

    @Column
    private Integer returnIncoming;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public Inventory(String inventoryDate, Integer stock, Integer outgoing, Integer fulfillment, Integer incoming, Integer returnIncoming, Product product) {
        this.inventoryDate = inventoryDate;
        this.product = product;
        this.stock = stock;
        this.outgoing = outgoing;
        this.fulfillment = fulfillment;
        this.incoming = incoming;
        this.returnIncoming = returnIncoming;
    }

    public void updateStock(int stock) {
        this.stock = stock;
    }

    public void updateOutgoing(int outgoing) {
        this.outgoing = outgoing;
    }

    public void updateFulfillment(int fulfillment) {
        this.fulfillment = fulfillment;
    }

    public void updateIncoming(int incoming) {
        this.incoming = incoming;
    }

    public void updateReturnIncoming(int returnIncoming) {
        this.returnIncoming = returnIncoming;
    }

    public void updateInventoryDate(String inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public void updateProduct(Product product) {
        this.product = product;
    }
}
