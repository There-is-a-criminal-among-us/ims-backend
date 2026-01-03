package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMappingRepository extends JpaRepository<ProductMapping, Long> {

    @EntityGraph(attributePaths = {"product"})
    List<ProductMapping> findByRawProduct(RawProduct rawProduct);
}
