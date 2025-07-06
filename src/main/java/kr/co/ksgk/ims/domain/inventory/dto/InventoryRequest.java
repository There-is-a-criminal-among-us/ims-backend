package kr.co.ksgk.ims.domain.inventory.dto;

import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import kr.co.ksgk.ims.domain.product.entity.Product;

public record InventoryRequest(
        Integer productId,
        Integer stock,
        Integer outgoing,
        Integer fulfillment,
        Integer incoming,
        Integer returnIncoming,
        String inventoryDate
) {
    public static Inventory toEntity(InventoryRequest inventoryRequest, Product product) {
        return Inventory.builder()
                .inventoryDate(inventoryRequest.inventoryDate())
                .stock(inventoryRequest.stock() != null ? inventoryRequest.stock() : 0)
                .outgoing(inventoryRequest.outgoing() != null ? inventoryRequest.outgoing() : 0)
                .fulfillment(inventoryRequest.fulfillment() != null ? inventoryRequest.fulfillment() : 0)
                .incoming(inventoryRequest.incoming() != null ? inventoryRequest.incoming() : 0)
                .returnIncoming(inventoryRequest.returnIncoming() != null ? inventoryRequest.returnIncoming() : 0)
                .product(product)
                .build();
    }
}
