package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<DailyStock, Long> {

    @EntityGraph(attributePaths = {"product", "product.brand", "product.brand.company"})
    @Query("""
                SELECT ds FROM DailyStock ds
                WHERE ds.product IN :products
                AND ds.stockDate BETWEEN :startDate AND :endDate
            """)
    List<DailyStock> findAllByProductsAndDateBetween(@Param("products") List<Product> products,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
