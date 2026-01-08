package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementTypeRepository extends JpaRepository<SettlementType, Long> {
}
