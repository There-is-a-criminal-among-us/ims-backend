package kr.co.ksgk.ims.domain.invoice.repository;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    @EntityGraph(attributePaths = {"invoice", "product"})
    Page<InvoiceProduct> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
                SELECT ip
                FROM InvoiceProduct ip
                JOIN ip.invoice i
                WHERE i.name LIKE %:keyword%
                   OR i.number LIKE %:keyword%
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumber(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
                SELECT ip FROM InvoiceProduct ip
                WHERE ip.product = :product
                AND ip.invoice.createdAt BETWEEN :startDateTime AND :endDateTime
            """)
    List<InvoiceProduct> findByProductAndInvoiceCreatedAtBetween(
            @Param("product") Product product,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE ip.product.id IN :productIds
            """)
    Page<InvoiceProduct> findByProductIdIn(@Param("productIds") Set<Long> productIds, Pageable pageable);

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword%)
            AND ip.product.id IN :productIds
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumberAndProductIdIn(
            @Param("keyword") String keyword,
            @Param("productIds") Set<Long> productIds,
            Pageable pageable
    );
}