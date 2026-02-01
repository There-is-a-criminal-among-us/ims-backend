package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementItemRepository extends JpaRepository<SettlementItem, Long> {

    @Query("SELECT DISTINCT i FROM SettlementItem i LEFT JOIN FETCH i.units ORDER BY i.displayOrder")
    List<SettlementItem> findAllWithUnits();

    @Query("SELECT DISTINCT i FROM SettlementItem i LEFT JOIN FETCH i.units WHERE i.calculationType IN :calculationTypes ORDER BY i.displayOrder")
    List<SettlementItem> findByCalculationTypesWithUnits(@Param("calculationTypes") List<CalculationType> calculationTypes);
}