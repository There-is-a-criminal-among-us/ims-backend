package kr.co.ksgk.ims.domain.delivery.repository;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryCustomRepository {
    
    @Query("""
        SELECT d FROM Delivery d
        WHERE d.rawProduct IN (
            SELECT pm.rawProduct FROM ProductMapping pm
            WHERE pm.product = :product
        )
        AND d.createdAt BETWEEN :startDateTime AND :endDateTime
    """)
    List<Delivery> findByRawProductProductMappingsProductAndCreatedAtBetween(
            @Param("product") Product product,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
