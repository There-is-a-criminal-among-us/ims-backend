package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Long> {

    @EntityGraph(attributePaths = {"settlement", "product", "settlementItem"})
    Optional<SettlementDetail> findById(Long id);

    @Query("""
            SELECT sd FROM SettlementDetail sd
            JOIN FETCH sd.product p
            JOIN FETCH p.brand b
            JOIN FETCH sd.settlementItem si
            WHERE sd.settlement.id = :settlementId
            """)
    List<SettlementDetail> findBySettlementIdWithDetails(@Param("settlementId") Long settlementId);
}
