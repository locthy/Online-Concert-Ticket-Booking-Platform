package com.geekup.flashsale.config;

import com.geekup.flashsale.entity.TicketCategory;
import com.geekup.flashsale.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final TicketCategoryRepository ticketCategoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Database if empty
        if (ticketCategoryRepository.count() == 0) {
            log.info("[SEEDER] Database empty. Seeding initial categories...");
            seedDatabase();
        }

        // 2. Cache Pre-warming: Sync DB Stock to Redis
        syncStockToRedis();
    }

    private void seedDatabase() {
        TicketCategory vip = new TicketCategory();
        vip.setConcertId(99L);
        vip.setName("VIP");
        vip.setPrice(1500000.0);
        vip.setTotalQuantity(100);

        TicketCategory standard = new TicketCategory();
        standard.setConcertId(99L);
        standard.setName("Standard");
        standard.setPrice(500000.0);
        standard.setTotalQuantity(500);

        ticketCategoryRepository.saveAll(List.of(vip, standard));
        log.info("[DB] Finished seeding 2 categories.");
    }

    private void syncStockToRedis() {
        log.info("[REDIS] Starting Cache Pre-warming...");

        List<TicketCategory> categories = ticketCategoryRepository.findAll();

        for (TicketCategory category : categories) {
            String stockKey = "ticket_stock:" + category.getId();

            // We set the stock in Redis from the DB value
            redisTemplate.opsForValue().set(stockKey, category.getTotalQuantity());

            log.info("[REDIS] Pre-warmed stock for {}: {} tickets",
                    category.getName(), category.getTotalQuantity());
        }

        log.info("[REDIS] Cache Pre-warming completed successfully!");
    }
}