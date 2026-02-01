package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.settlement.entity.Settlement;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByYearAndMonthAndCompany(Integer year, Integer month, Company company);

    @Query("SELECT s FROM Settlement s LEFT JOIN FETCH s.details WHERE s.year = :year AND s.month = :month AND s.company = :company")
    Optional<Settlement> findByYearAndMonthAndCompanyWithDetails(@Param("year") Integer year,
                                                                @Param("month") Integer month,
                                                                @Param("company") Company company);

    @EntityGraph(attributePaths = {"company"})
    List<Settlement> findByYearAndMonth(Integer year, Integer month);

    @Query("""
            SELECT s FROM Settlement s
            JOIN FETCH s.company c
            WHERE s.year = :year AND s.month = :month AND c.id = :companyId AND s.status = :status
            """)
    List<Settlement> findByYearAndMonthAndCompanyIdAndStatus(@Param("year") Integer year,
                                                              @Param("month") Integer month,
                                                              @Param("companyId") Long companyId,
                                                              @Param("status") SettlementStatus status);
}
