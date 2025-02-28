package com.menghor.smart_shop.scheduled;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void removeExpiredPromotions() {
        LocalDate now = LocalDate.now();
        log.info("Removing expired promotions");
        log.info("Current date: {}", now);
        List<ProductEntity> expiredProducts = productRepository.findByDiscountEndDateBefore(now);

        for (ProductEntity product : expiredProducts) {
            // Clear expired promotions
            product.setDiscountType(null);
            product.setDiscountValue(null);
            product.setDiscountStartDate(null);
            product.setDiscountEndDate(null);
            log.info("Removing promotion from product: {}", product);
            productRepository.save(product);
        }
    }
}