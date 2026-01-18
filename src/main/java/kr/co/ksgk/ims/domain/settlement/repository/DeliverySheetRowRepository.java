package kr.co.ksgk.ims.domain.settlement.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.settlement.entity.DeliverySheetRow;
import kr.co.ksgk.ims.domain.settlement.entity.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliverySheetRowRepository extends JpaRepository<DeliverySheetRow, Long> {

    List<DeliverySheetRow> findByYearAndMonth(Integer year, Integer month);

    @Modifying
    @Query("DELETE FROM DeliverySheetRow d WHERE d.year = :year AND d.month = :month")
    void deleteByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    long countByYearAndMonthAndProduct(Integer year, Integer month, Product product);

    long countByYearAndMonthAndProductAndWorkType(Integer year, Integer month, Product product, WorkType workType);

    @Query("""
            SELECT d FROM DeliverySheetRow d
            JOIN FETCH d.product p
            WHERE d.year = :year AND d.month = :month AND d.product = :product
            """)
    List<DeliverySheetRow> findByYearAndMonthAndProduct(@Param("year") Integer year,
                                                         @Param("month") Integer month,
                                                         @Param("product") Product product);
}
