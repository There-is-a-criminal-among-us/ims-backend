package kr.co.ksgk.ims.domain.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionType;
import kr.co.ksgk.ims.global.annotation.NotZero;

import java.time.LocalDate;

public record TransactionRequest(
        Long productId,
        String enumName,
        @NotNull  @NotZero
        Integer quantity,
        String note,
        LocalDate scheduledDate
) {
    public Transaction toEntity(Product product, TransactionType transactionType) {
        return Transaction.builder()
                .product(product)
                .transactionType(transactionType)
                .quantity(quantity)
                .note(note)
                .scheduledDate(scheduledDate)
                .build();
    }
}
