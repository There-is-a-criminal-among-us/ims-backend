package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.DeliveryItemMapping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryItemMappingRepository extends JpaRepository<DeliveryItemMapping, Long> {

    @EntityGraph(attributePaths = {"product", "rawDeliveryItem"})
    @Query("""
            SELECT dim
            FROM DeliveryItemMapping dim
            WHERE dim.rawDeliveryItem.rawName = :rawName
            """)
    List<DeliveryItemMapping> findProductsByRawName(@Param("rawName") String rawName);
}
