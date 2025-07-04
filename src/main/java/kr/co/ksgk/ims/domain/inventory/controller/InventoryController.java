package kr.co.ksgk.ims.domain.inventory.controller;

import kr.co.ksgk.ims.domain.inventory.dto.InventoryRequest;
import kr.co.ksgk.ims.domain.inventory.service.InventoryService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllInventories() {
        return SuccessResponse.ok(inventoryService.getAllInventories());
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createInventory(@RequestBody InventoryRequest request) {
        return SuccessResponse.created(inventoryService.createInventory(request));
    }

    @PatchMapping("/{inventoryId}")
    public ResponseEntity<SuccessResponse<?>> updateInventory(@PathVariable int inventoryId, @RequestBody InventoryRequest request) {
        return SuccessResponse.ok(inventoryService.updateInventory(inventoryId, request));
    }
}
