package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementTypeRepository extends JpaRepository<SettlementType, Long> {

    @Query("SELECT DISTINCT t FROM SettlementType t " +
            "LEFT JOIN FETCH t.categories c " +
            "LEFT JOIN FETCH c.items i " +
            "LEFT JOIN FETCH i.units " +
            "LEFT JOIN FETCH t.directItems di " +
            "LEFT JOIN FETCH di.units " +
            "WHERE t.id = :id")
    Optional<SettlementType> findByIdWithAll(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM SettlementType t " +
            "LEFT JOIN FETCH t.categories c " +
            "LEFT JOIN FETCH c.items ci " +
            "LEFT JOIN FETCH ci.units " +
            "LEFT JOIN FETCH t.directItems di " +
            "LEFT JOIN FETCH di.units " +
            "ORDER BY t.displayOrder")
    List<SettlementType> findAllWithHierarchy();
}
