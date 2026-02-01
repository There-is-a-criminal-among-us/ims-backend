package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.ChargeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeCategoryRepository extends JpaRepository<ChargeCategory, Long> {
}
