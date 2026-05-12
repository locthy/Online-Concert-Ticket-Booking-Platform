package com.geekup.flashsale.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryResponse {
    private Long categoryId;
    private String categoryName;
    private Long concertId;
    private Integer totalQuantity;
    private Integer remainingQuantity;
}

