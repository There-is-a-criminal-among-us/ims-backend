package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    Optional<Product> findById(Long productId);

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    Page<Product> findByNameContaining(String name, Pageable pageable);

    @Query("""
            SELECT p.id FROM Product p
            WHERE p.brand.id IN :brandIds
            """)
    Set<Long> findIdsByBrandIdIn(@Param("brandIds") Set<Long> brandIds);

    @Query("""
            SELECT p.id FROM Product p
            WHERE p.brand.company.id IN :companyIds
            """)
    Set<Long> findIdsByCompanyIdIn(@Param("companyIds") Set<Long> companyIds);
}
