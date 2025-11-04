package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionCustomRepository {

    List<Transaction> findByProductAndCreatedAtBetweenAndTransactionStatus(
            Product product,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            TransactionStatus transactionStatus
    );

    List<Transaction> findByProductAndUpdatedAtBetweenAndTransactionStatus(
            Product product,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            TransactionStatus transactionStatus
    );
}
