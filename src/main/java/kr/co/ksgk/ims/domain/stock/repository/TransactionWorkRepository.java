package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.TransactionWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionWorkRepository extends JpaRepository<TransactionWork, Long> {

    List<TransactionWork> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);
}