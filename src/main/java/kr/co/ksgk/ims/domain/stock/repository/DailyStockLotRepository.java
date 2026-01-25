package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.DailyStockLot;
import kr.co.ksgk.ims.domain.stock.entity.StockLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyStockLotRepository extends JpaRepository<DailyStockLot, Long> {

    /**
     * 특정 상품의 특정 기간 일별 로트 스냅샷 조회
     */
    @Query("""
            SELECT dsl FROM DailyStockLot dsl
            WHERE dsl.product = :product
            AND dsl.stockDate BETWEEN :startDate AND :endDate
            ORDER BY dsl.stockDate, dsl.inboundDate
            """)
    List<DailyStockLot> findByProductAndDateBetween(
            @Param("product") Product product,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 여러 상품의 특정 기간 일별 로트 스냅샷 조회
     */
    @Query("""
            SELECT dsl FROM DailyStockLot dsl
            WHERE dsl.product IN :products
            AND dsl.stockDate BETWEEN :startDate AND :endDate
            ORDER BY dsl.product.id, dsl.stockDate, dsl.inboundDate
            """)
    List<DailyStockLot> findByProductsAndDateBetween(
            @Param("products") List<Product> products,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 특정 날짜의 모든 일별 로트 스냅샷 조회
     */
    List<DailyStockLot> findByStockDate(LocalDate stockDate);

    /**
     * 특정 로트 + 날짜의 스냅샷 존재 여부
     */
    boolean existsByStockLotAndStockDate(StockLot stockLot, LocalDate stockDate);

    /**
     * 특정 상품의 특정 날짜 스냅샷 조회
     */
    List<DailyStockLot> findByProductAndStockDate(Product product, LocalDate stockDate);

    /**
     * 정산용: 특정 기간의 무료 기간 초과 로트만 조회
     */
    @Query("""
            SELECT dsl FROM DailyStockLot dsl
            WHERE dsl.product = :product
            AND dsl.stockDate BETWEEN :startDate AND :endDate
            AND dsl.daysFromInbound > :freePeriodDays
            ORDER BY dsl.stockDate, dsl.inboundDate
            """)
    List<DailyStockLot> findBillableByProductAndDateBetween(
            @Param("product") Product product,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("freePeriodDays") int freePeriodDays);

    /**
     * 특정 상품의 특정 기간 일별 총 과금 수량 합계
     */
    @Query("""
            SELECT COALESCE(SUM(dsl.quantity), 0) FROM DailyStockLot dsl
            WHERE dsl.product = :product
            AND dsl.stockDate BETWEEN :startDate AND :endDate
            AND dsl.daysFromInbound > :freePeriodDays
            """)
    Integer getTotalBillableQuantity(
            @Param("product") Product product,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("freePeriodDays") int freePeriodDays);
}
