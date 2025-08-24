package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TransactionCustomRepository {

    Page<Transaction> searchTransactions(String search, List<String> types, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Page<Transaction> searchTransactionsByProductIds(String search, List<String> types, LocalDate startDate, LocalDate endDate, Set<Long> productIds, Pageable pageable);
}
