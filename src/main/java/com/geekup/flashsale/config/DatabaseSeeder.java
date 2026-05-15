package com.geekup.flashsale.config;

import com.geekup.flashsale.entity.Concert;
import com.geekup.flashsale.entity.TicketCategory;
import com.geekup.flashsale.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Seeds baseline concert and ticket data and synchronizes Redis inventory keys.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * Initializes startup seed data and cache keys.
     *
     * @param args startup arguments
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("[DB Seeder] Starting database initialization...");

        Concert concert;

        // Step 1: Ensure baseline relational data exists.
        if (concertRepository.count() == 0) {
            log.info("[DB Seeder] No concert found in DB. Creating new one...");
            concert = new Concert();
            concert.setName("BlackPink World Tour");
            concert.setEventDate(LocalDateTime.now().plusDays(30));
            concert.setStatus("OPEN");

            TicketCategory vip = new TicketCategory();
            vip.setName("VIP");
            vip.setPrice(1500000.0);
            vip.setTotalQuantity(100);
            vip.setAvailableQuantity(100);

            TicketCategory standard = new TicketCategory();
            standard.setName("Standard");
            standard.setPrice(500000.0);
            standard.setTotalQuantity(500);
            standard.setAvailableQuantity(500);

            concert.addTicketCategory(vip);
            concert.addTicketCategory(standard);

            concert = concertRepository.save(concert);
            log.info("[DB] Saved Concert and Tickets to PostgreSQL.");
        } else {
            log.info("[DB Seeder] Concert already exists in DB. Fetching...");
            concert = concertRepository.findAll().get(0);
        }

        // Step 2: Ensure Redis contains event status and stock keys.
        String eventKey = "event:" + concert.getId();

        // Rebuild cache keys if Redis was restarted or flushed.
        if (Boolean.FALSE.equals(redisTemplate.hasKey(eventKey))) {
            log.info("[REDIS] Missing data in Redis. Syncing from DB...");

            redisTemplate.opsForValue().set(eventKey, "OPEN");
            log.info("[REDIS] Opened event: {}", eventKey);

            for (TicketCategory category : concert.getTicketCategories()) {
                String stockKey = "ticket_stock:" + category.getId();
                redisTemplate.opsForValue().set(stockKey, String.valueOf(category.getAvailableQuantity()));
                log.info("[REDIS] Seeded stock for {}: {} tickets", stockKey, category.getAvailableQuantity());
            }

            log.info("[REDIS] Successfully synced Database to Redis!");
        } else {
            log.info("[REDIS] Data is already present in Redis. All good!");
        }
    }
}
