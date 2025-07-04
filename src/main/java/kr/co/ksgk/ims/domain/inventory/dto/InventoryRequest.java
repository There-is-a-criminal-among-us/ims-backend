package kr.co.ksgk.ims.domain.inventory.dto;

import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import kr.co.ksgk.ims.domain.product.entity.Product;

public record InventoryRequest(
        Integer productId,
        Integer quantity,
        String inventoryDate
) {
    public static Inventory toEntity(InventoryRequest inventoryRequest, Product product) {
        return Inventory.builder()
                .inventoryDate(inventoryRequest.inventoryDate())
                .quantity(inventoryRequest.quantity())
                .product(product)
                .build();
    }
}
