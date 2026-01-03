package kr.co.ksgk.ims.domain.stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockCacheInvalidator {

    private final RealTimeStockService realTimeStockService;

    @Async
    public void invalidateCacheForProduct(Long productId) {
        realTimeStockService.invalidateTodayCache(productId);
        log.debug("제품 캐시 무효화 - Product: {}", productId);
    }

    @Async
    public void invalidateCacheForProducts(List<Long> productIds) {
        productIds.forEach(productId -> {
            realTimeStockService.invalidateTodayCache(productId);
            log.debug("제품 캐시 무효화 - Product: {}", productId);
        });
    }

    public void invalidateCacheForProductIfToday(Long productId) {
        realTimeStockService.invalidateTodayCache(productId);
        log.debug("오늘 날짜 제품 캐시 무효화 - Product: {}", productId);
    }

    public void invalidateCacheForProductsIfToday(List<Long> productIds) {
        productIds.forEach(this::invalidateCacheForProductIfToday);
    }
}