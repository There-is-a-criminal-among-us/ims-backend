package kr.co.ksgk.ims.domain.brand.repository;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @EntityGraph(attributePaths = {"company"})
    Optional<Brand> findById(Long id);

    @EntityGraph(attributePaths = {"company"})
    Page<Brand> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"company"})
    Page<Brand> findByNameContaining(String name, Pageable pageable);
}
