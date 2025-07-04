package kr.co.ksgk.ims.domain.inventory.dto;

import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import kr.co.ksgk.ims.domain.product.dto.ProductResponse;
import lombok.Builder;

@Builder
public record InventoryResponse(
        int id,
        String inventoryDate,
        int quantity,
        ProductResponse product
) {
    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .inventoryDate(inventory.getInventoryDate())
                .quantity(inventory.getQuantity())
                .product(ProductResponse.from(inventory.getProduct()))
                .build();
    }
}
