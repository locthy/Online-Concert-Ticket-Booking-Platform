package com.geekup.flashsale.service;

import com.geekup.flashsale.dto.response.InventoryResponse;
import com.geekup.flashsale.entity.TicketCategory;
import com.geekup.flashsale.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Reads inventory from Redis with database fallback.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TicketCategoryRepository ticketCategoryRepository;

    /**
     * Returns stock information for a category.
     *
     * @param categoryId ticket category id
     * @return inventory response
     */
    public InventoryResponse getInventory(Long categoryId) {
        TicketCategory category = ticketCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("TicketCategory not found: " + categoryId));

        String stockKey = "ticket_stock:" + categoryId;
        Object remainingObj = redisTemplate.opsForValue().get(stockKey);

        Integer remaining = null;
        if (remainingObj instanceof Integer i) {
            remaining = i;
        } else if (remainingObj instanceof Long l) {
            remaining = Math.toIntExact(l);
        } else if (remainingObj instanceof String s) {
            remaining = Integer.valueOf(s);
        }

        if (remaining == null) {
            remaining = category.getTotalQuantity();
        }

        return new InventoryResponse(
                category.getId(),
                category.getName(),
                category.getConcert().getId(),
                category.getTotalQuantity(),
                remaining
        );
    }
}
