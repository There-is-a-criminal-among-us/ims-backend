package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawProductRepository extends JpaRepository<RawProduct, Long> {

    Page<RawProduct> findAll(Pageable pageable);

    Page<RawProduct> findByNameContaining(String name, Pageable pageable);
}
