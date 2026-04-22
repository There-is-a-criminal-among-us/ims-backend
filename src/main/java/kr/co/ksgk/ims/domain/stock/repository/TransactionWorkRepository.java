package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.TransactionWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionWorkRepository extends JpaRepository<TransactionWork, Long> {

    List<TransactionWork> findByTransactionId(Long transactionId);

    void deleteByTransactionId(Long transactionId);

    @Query("""
            SELECT tw FROM TransactionWork tw
            JOIN FETCH tw.transaction t
            JOIN FETCH t.product
            JOIN FETCH tw.settlementItem si
            WHERE t.workDate BETWEEN :startDate AND :endDate
            """)
    List<TransactionWork> findByWorkDateBetween(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}