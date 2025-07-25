package kr.co.ksgk.ims.domain.invoice.repository;

import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    Page<InvoiceProduct> findAll(Pageable pageable);

    @Query("""
                SELECT ip
                FROM InvoiceProduct ip
                JOIN ip.invoice i
                WHERE i.name LIKE %:keyword%
                   OR i.number LIKE %:keyword%
            """)
    Page<InvoiceProduct> findInvoiceProductByNameOrNumber(@Param("keyword") String keyword, Pageable pageable);
}