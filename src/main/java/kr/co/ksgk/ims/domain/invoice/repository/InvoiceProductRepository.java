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
                JOIN ip.product p
                WHERE i.name LIKE %:keyword%
                   OR i.number LIKE %:keyword%
                   OR p.name LIKE %:keyword%
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
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND ip.product.id IN :productIds
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumberOrInvoiceNumberAndProductIdIn(
            @Param("keyword") String keyword,
            @Param("productIds") Set<Long> productIds,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE YEAR(ip.invoice.createdAt) = :year
            AND MONTH(ip.invoice.createdAt) = :month
            """)
    Page<InvoiceProduct> findAllByYearAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND YEAR(i.createdAt) = :year
            AND MONTH(i.createdAt) = :month
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumberAndYearAndMonth(
            @Param("keyword") String keyword,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE ip.product.id IN :productIds
            AND YEAR(ip.invoice.createdAt) = :year
            AND MONTH(ip.invoice.createdAt) = :month
            """)
    Page<InvoiceProduct> findByProductIdInAndYearAndMonth(
            @Param("productIds") Set<Long> productIds,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND ip.product.id IN :productIds
            AND YEAR(i.createdAt) = :year
            AND MONTH(i.createdAt) = :month
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumberOrInvoiceNumberAndProductIdInAndYearAndMonth(
            @Param("keyword") String keyword,
            @Param("productIds") Set<Long> productIds,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            ORDER BY ip.invoice.createdAt DESC
            """)
    List<InvoiceProduct> findAllOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE i.name LIKE %:keyword%
               OR i.number LIKE %:keyword%
               OR p.name LIKE %:keyword%
            ORDER BY i.createdAt DESC
            """)
    List<InvoiceProduct> findInvoiceProductByNameOrNumber(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE YEAR(ip.invoice.createdAt) = :year
            AND MONTH(ip.invoice.createdAt) = :month
            ORDER BY ip.invoice.createdAt DESC
            """)
    List<InvoiceProduct> findAllByYearAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND YEAR(i.createdAt) = :year
            AND MONTH(i.createdAt) = :month
            ORDER BY i.createdAt DESC
            """)
    List<InvoiceProduct> findInvoiceProductByNameOrNumberAndYearAndMonth(
            @Param("keyword") String keyword,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE ip.product.id IN :productIds
            ORDER BY ip.invoice.createdAt DESC
            """)
    List<InvoiceProduct> findByProductIdInOrderByCreatedAtDesc(@Param("productIds") Set<Long> productIds);

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND ip.product.id IN :productIds
            ORDER BY i.createdAt DESC
            """)
    List<InvoiceProduct> findInvoiceProductByNameOrNumberOrInvoiceNumberAndProductIdIn(
            @Param("keyword") String keyword,
            @Param("productIds") Set<Long> productIds
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip FROM InvoiceProduct ip
            WHERE ip.product.id IN :productIds
            AND YEAR(ip.invoice.createdAt) = :year
            AND MONTH(ip.invoice.createdAt) = :month
            ORDER BY ip.invoice.createdAt DESC
            """)
    List<InvoiceProduct> findByProductIdInAndYearAndMonth(
            @Param("productIds") Set<Long> productIds,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @EntityGraph(attributePaths = {"invoice", "product"})
    @Query("""
            SELECT ip
            FROM InvoiceProduct ip
            JOIN ip.invoice i
            JOIN ip.product p
            WHERE (i.name LIKE %:keyword% OR i.number LIKE %:keyword% OR p.name LIKE %:keyword%)
            AND ip.product.id IN :productIds
            AND YEAR(i.createdAt) = :year
            AND MONTH(i.createdAt) = :month
            ORDER BY i.createdAt DESC
            """)
    List<InvoiceProduct> findInvoiceProductByNameOrNumberOrInvoiceNumberAndProductIdInAndYearAndMonth(
            @Param("keyword") String keyword,
            @Param("productIds") Set<Long> productIds,
            @Param("year") Integer year,
            @Param("month") Integer month
    );
}