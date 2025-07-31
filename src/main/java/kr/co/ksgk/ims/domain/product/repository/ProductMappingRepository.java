package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMappingRepository extends JpaRepository<ProductMapping, Long> {

    @EntityGraph(attributePaths = {"product", "rawDeliveryItem"})
    @Query("""
            SELECT dim
            FROM ProductMapping dim
            WHERE dim.rawDeliveryItem.rawName = :rawName
            """)
    List<ProductMapping> findProductsByRawName(@Param("rawName") String rawName);
}
