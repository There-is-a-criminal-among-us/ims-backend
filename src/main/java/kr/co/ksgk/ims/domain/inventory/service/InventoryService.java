package kr.co.ksgk.ims.domain.inventory.service;

import kr.co.ksgk.ims.domain.inventory.dto.InventoryRequest;
import kr.co.ksgk.ims.domain.inventory.dto.InventoryResponse;
import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import kr.co.ksgk.ims.domain.inventory.repository.InventoryRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        Inventory inventory = InventoryRequest.toEntity(request, product);
        return InventoryResponse.from(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponse updateInventory(int inventoryId, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));
        if (request.stock() != null) {
            inventory.updateStock(request.stock());
        }
        if (request.outgoing() != null) {
            inventory.updateOutgoing(request.outgoing());
        }
        if (request.fulfillment() != null) {
            inventory.updateFulfillment(request.fulfillment());
        }
        if (request.incoming() != null) {
            inventory.updateIncoming(request.incoming());
        }
        if (request.returnIncoming() != null) {
            inventory.updateReturnIncoming(request.returnIncoming());
        }
        if (request.inventoryDate() != null) {
            inventory.updateInventoryDate(request.inventoryDate());
        }
        if (request.productId() != null) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
            inventory.updateProduct(product);
        }
        return InventoryResponse.from(inventory);
    }
}
