package kr.co.ksgk.ims.domain.inventory.dto;

import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import kr.co.ksgk.ims.domain.product.dto.ProductResponse;
import lombok.Builder;

@Builder
public record InventoryResponse(
        int id,
        int stock,
        int outgoing,
        int fulfillment,
        int incoming,
        int returnIncoming,
        String inventoryDate,
        ProductResponse product
) {
    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .stock(inventory.getStock())
                .outgoing(inventory.getOutgoing())
                .fulfillment(inventory.getFulfillment())
                .incoming(inventory.getIncoming())
                .returnIncoming(inventory.getReturnIncoming())
                .inventoryDate(inventory.getInventoryDate())
                .product(ProductResponse.from(inventory.getProduct()))
                .build();
    }
}
