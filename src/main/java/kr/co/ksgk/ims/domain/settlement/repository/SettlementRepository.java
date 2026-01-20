package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.settlement.entity.Settlement;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByYearAndMonthAndBrand(Integer year, Integer month, Brand brand);

    @Query("SELECT s FROM Settlement s LEFT JOIN FETCH s.details WHERE s.year = :year AND s.month = :month AND s.brand = :brand")
    Optional<Settlement> findByYearAndMonthAndBrandWithDetails(@Param("year") Integer year,
                                                                @Param("month") Integer month,
                                                                @Param("brand") Brand brand);

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    List<Settlement> findByYearAndMonth(Integer year, Integer month);

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    List<Settlement> findByYearAndMonthAndStatus(Integer year, Integer month, SettlementStatus status);

    @Query("""
            SELECT s FROM Settlement s
            JOIN FETCH s.brand b
            JOIN FETCH b.company c
            WHERE s.year = :year AND s.month = :month AND c.id = :companyId
            """)
    List<Settlement> findByYearAndMonthAndCompanyId(@Param("year") Integer year,
                                                     @Param("month") Integer month,
                                                     @Param("companyId") Long companyId);

    @Query("""
            SELECT s FROM Settlement s
            JOIN FETCH s.brand b
            JOIN FETCH b.company c
            WHERE s.year = :year AND s.month = :month AND c.id = :companyId AND s.status = :status
            """)
    List<Settlement> findByYearAndMonthAndCompanyIdAndStatus(@Param("year") Integer year,
                                                              @Param("month") Integer month,
                                                              @Param("companyId") Long companyId,
                                                              @Param("status") SettlementStatus status);
}
