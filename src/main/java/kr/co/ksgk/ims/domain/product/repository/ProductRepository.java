package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
                SELECT p FROM Product p
                JOIN FETCH p.brand b
                JOIN FETCH b.company c
                WHERE b.id IN :brandIds OR c.id IN :companyIds
            """)
    List<Product> findByCompanyIdInOrBrandIdIn(@Param("companyIds") List<Long> companyIds,
                                               @Param("brandIds") List<Long> brandIds);
}
