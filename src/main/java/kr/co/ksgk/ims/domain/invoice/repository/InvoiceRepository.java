package kr.co.ksgk.ims.domain.invoice.repository;

import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
