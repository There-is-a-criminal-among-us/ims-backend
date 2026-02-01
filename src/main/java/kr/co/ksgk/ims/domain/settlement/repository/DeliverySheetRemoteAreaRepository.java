package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.settlement.entity.DeliverySheetRemoteArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliverySheetRemoteAreaRepository extends JpaRepository<DeliverySheetRemoteArea, Long> {

    List<DeliverySheetRemoteArea> findByYearAndMonthOrderByIdAsc(Integer year, Integer month);

    @Modifying
    @Query("DELETE FROM DeliverySheetRemoteArea d WHERE d.year = :year AND d.month = :month")
    void deleteByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
}
