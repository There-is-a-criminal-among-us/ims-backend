package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionCustomRepository {

    Page<Transaction> searchTransactions(String search, TransactionGroup type, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
