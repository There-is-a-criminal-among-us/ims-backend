package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.StockLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

    /**
     * 특정 상품의 잔여 수량이 있는 로트를 입고일 순으로 조회 (FIFO)
     */
    @Query("""
            SELECT sl FROM StockLot sl
            WHERE sl.product = :product
            AND sl.remainingQuantity > 0
            ORDER BY sl.inboundDate ASC, sl.id ASC
            """)
    List<StockLot> findByProductWithRemainingOrderByInboundDateAsc(@Param("product") Product product);

    /**
     * 특정 상품의 모든 로트 조회 (입고일 역순)
     */
    List<StockLot> findByProductOrderByInboundDateDesc(Product product);

    /**
     * 잔여 수량이 있는 모든 로트 조회
     */
    @Query("""
            SELECT sl FROM StockLot sl
            WHERE sl.remainingQuantity > 0
            ORDER BY sl.product.id, sl.inboundDate ASC
            """)
    List<StockLot> findAllWithRemaining();

    /**
     * 특정 상품들의 잔여 수량이 있는 로트 조회
     */
    @Query("""
            SELECT sl FROM StockLot sl
            WHERE sl.product IN :products
            AND sl.remainingQuantity > 0
            ORDER BY sl.product.id, sl.inboundDate ASC
            """)
    List<StockLot> findByProductsWithRemaining(@Param("products") List<Product> products);

    /**
     * 특정 상품의 총 잔여 수량
     */
    @Query("""
            SELECT COALESCE(SUM(sl.remainingQuantity), 0) FROM StockLot sl
            WHERE sl.product = :product
            """)
    Integer getTotalRemainingByProduct(@Param("product") Product product);
}
