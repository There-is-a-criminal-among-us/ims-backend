package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionCustomRepository {
}
