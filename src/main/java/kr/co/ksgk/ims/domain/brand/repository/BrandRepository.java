package kr.co.ksgk.ims.domain.brand.repository;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @EntityGraph(attributePaths = {"company"})
    List<Brand> findAll();
}
