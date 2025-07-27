package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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
    Optional<Product> findByIdAndDeletedAtIsNull(Long id); // 논리적으로 삭제되지 않은 제품 조회
    List<Product> findByDeletedAtIsNull(); // 논리적으로 삭제되지 않은 모든 제품 조회
}
