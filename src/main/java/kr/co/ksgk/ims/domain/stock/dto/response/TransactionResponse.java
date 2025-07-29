package kr.co.ksgk.ims.domain.stock.dto.response;

import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionGroup;
import kr.co.ksgk.ims.domain.stock.entity.TransactionStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TransactionResponse(
        long id,
        TransactionGroup type,
        LocalDate scheduledDate,
        String companyName,
        String brandName,
        String productName,
        int quantity,
        String note,
        TransactionStatus status,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getTransactionType().getGroupType())
                .scheduledDate(transaction.getScheduledDate())
                .companyName(transaction.getProduct().getBrand().getCompany().getName())
                .brandName(transaction.getProduct().getBrand().getName())
                .productName(transaction.getProduct().getName())
                .quantity(transaction.getQuantity())
                .note(transaction.getNote())
                .status(transaction.getTransactionStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
