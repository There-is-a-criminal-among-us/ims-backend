package kr.co.ksgk.ims.domain.stock.repository;

import kr.co.ksgk.ims.domain.stock.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {

    Optional<TransactionType> findByName(String name);
}
