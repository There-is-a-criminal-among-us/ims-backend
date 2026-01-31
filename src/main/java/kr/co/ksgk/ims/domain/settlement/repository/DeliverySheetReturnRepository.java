package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.DeliverySheetReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliverySheetReturnRepository extends JpaRepository<DeliverySheetReturn, Long> {

    List<DeliverySheetReturn> findByYearAndMonthOrderByIdAsc(Integer year, Integer month);

    @Modifying
    @Query("DELETE FROM DeliverySheetReturn d WHERE d.year = :year AND d.month = :month")
    void deleteByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
}
