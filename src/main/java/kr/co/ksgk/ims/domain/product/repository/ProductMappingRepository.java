package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMappingRepository extends JpaRepository<ProductMapping, Long> {

    @EntityGraph(attributePaths = {"product", "rawProduct"})
    @Query("""
            SELECT pm
            FROM ProductMapping pm
            WHERE pm.rawProduct.name = :rawName
            """)
    List<ProductMapping> findProductsByRawName(@Param("rawName") String rawName);

    @EntityGraph(attributePaths = {"product"})
    List<ProductMapping> findByRawProduct(RawProduct rawProduct);
}
