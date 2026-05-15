package com.geekup.flashsale.controller;

import com.geekup.flashsale.dto.response.InventoryResponse;
import com.geekup.flashsale.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes inventory-read endpoints for ticket categories.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Returns current inventory for a ticket category.
     *
     * @param categoryId ticket category id
     * @return inventory view model
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(inventoryService.getInventory(categoryId));
    }
}
