package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
                SELECT p FROM Product p
                JOIN FETCH p.brand b
                JOIN FETCH b.company c
                WHERE b.id IN :brandIds OR c.id IN :companyIds
            """)
    List<Product> findByCompanyIdInOrBrandIdIn(@Param("companyIds") List<Long> companyIds,
                                               @Param("brandIds") List<Long> brandIds);

    Page<Product> findByNameContaining(String name, Pageable pageable);
}
