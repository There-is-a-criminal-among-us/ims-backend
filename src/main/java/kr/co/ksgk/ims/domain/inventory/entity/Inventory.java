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

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public Inventory(String inventoryDate, Integer quantity, Product product) {
        this.inventoryDate = inventoryDate;
        this.quantity = quantity;
        this.product = product;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateInventoryDate(String inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public void updateProduct(Product product) {
        this.product = product;
    }
}
