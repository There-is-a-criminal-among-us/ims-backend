package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 특정 브랜드에 속한 제품을 찾는 쿼리 메소드 추가 (선택 사항)
    List<Product> findByBrandId(Long brandId);

    // 삭제되지 않은 제품만 조회하는 쿼리 메소드 추가 (선택 사항)
    List<Product> findByDeletedAtIsNull();

    // ID로 삭제되지 않은 제품을 찾는 쿼리 메소드 추가 (선택 사항)
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
}