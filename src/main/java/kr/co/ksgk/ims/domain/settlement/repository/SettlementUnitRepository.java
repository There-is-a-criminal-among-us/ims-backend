package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementUnitRepository extends JpaRepository<SettlementUnit, Long> {
}