package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.DailyStockCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStockCacheRepository extends CrudRepository<DailyStockCache, String> {

    Optional<DailyStockCache> findByProductIdAndStockDate(Long productId, LocalDate stockDate);
    
    List<DailyStockCache> findByProductIdInAndStockDate(List<Long> productIds, LocalDate stockDate);
    
    void deleteByProductIdAndStockDate(Long productId, LocalDate stockDate);
    
    void deleteByProductId(Long productId);

    boolean existsByProductIdAndStockDate(Long productId, LocalDate stockDate);
}