package kr.co.ksgk.ims.domain.brand.repository;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Page<Brand> findByNameContaining(String name, Pageable pageable);
}
