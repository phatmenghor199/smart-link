package com.menghor.smart_shop.scheduled;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void removeExpiredPromotions() {
        LocalDate now = LocalDate.now();
        log.info("Removing expired promotions");
        log.info("Current date: {}", now);
        List<ProductEntity> expiredProducts = productRepository.findByDiscountEndDateBefore(now);

        for (ProductEntity product : expiredProducts) {
            product.resetDiscount();
            log.info("Resetting discount for product: {}", product.getName());
            productRepository.save(product);
        }

        // Reset expired discounts for product sizes
        List<ProductSizeEntity> expiredSizes = productSizeRepository.findByDiscountEndDateBefore(now);
        for (ProductSizeEntity size : expiredSizes) {
            size.resetDiscount();
            log.info("Resetting discount for product size: {}", size.getSize());
            productSizeRepository.save(size);
        }
    }
}