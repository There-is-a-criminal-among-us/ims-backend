package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Integer quantity;

    private LocalDate scheduledDate;

    @Lob
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;

    @Builder
    public Transaction(Product product, TransactionType transactionType, Integer quantity, LocalDate scheduledDate, String note, TransactionStatus transactionStatus) {
        this.product = product;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.scheduledDate = scheduledDate;
        this.note = note;
        this.transactionStatus = transactionStatus;
    }

    public void confirm() {
        if (this.transactionStatus != TransactionStatus.PENDING) {
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_PENDING);
        }
        this.transactionStatus = TransactionStatus.CONFIRMED;
    }
}
