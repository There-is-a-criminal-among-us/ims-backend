package kr.co.ksgk.ims.domain.invoice.repository;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = {"invoiceProducts", "invoiceProducts.product"})
    Optional<Invoice> findById(Long invoiceId);

    Optional<Invoice> findByNumber(String number);

    @Query("SELECT i FROM Invoice i WHERE REPLACE(i.number, '-', '') = :normalized")
    Optional<Invoice> findByNormalizedNumber(@Param("normalized") String normalized);
}