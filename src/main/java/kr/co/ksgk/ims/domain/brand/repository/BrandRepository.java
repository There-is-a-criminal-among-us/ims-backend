package kr.co.ksgk.ims.domain.brand.repository;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByCompanyIdAndDeletedAtIsNull(Long companyId);
    Optional<Brand> findByIdAndDeletedAtIsNull(Long id);
}
