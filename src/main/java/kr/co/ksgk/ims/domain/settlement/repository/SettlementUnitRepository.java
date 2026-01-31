package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementUnitRepository extends JpaRepository<SettlementUnit, Long> {

    @Query("SELECT u FROM SettlementUnit u JOIN FETCH u.item i WHERE i.calculationType IN :calculationTypes ORDER BY i.displayOrder, u.displayOrder")
    List<SettlementUnit> findByCalculationTypes(@Param("calculationTypes") List<CalculationType> calculationTypes);

    @Query("SELECT u FROM SettlementUnit u JOIN FETCH u.item i ORDER BY i.displayOrder, u.displayOrder")
    List<SettlementUnit> findAllWithItem();
}