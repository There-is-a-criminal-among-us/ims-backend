package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionRequest;
import kr.co.ksgk.ims.domain.stock.dto.response.PagingTransactionResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.TransactionResponse;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionGroup;
import kr.co.ksgk.ims.domain.stock.entity.TransactionType;
import kr.co.ksgk.ims.domain.stock.repository.TransactionRepository;
import kr.co.ksgk.ims.domain.stock.repository.TransactionTypeRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final ProductRepository productRepository;

    public PagingTransactionResponse getAllTransactions(
            String search, List<String> types, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Transaction> pageTransaction = transactionRepository.searchTransactions(search, types, startDate, endDate, pageable);
        List<TransactionResponse> transactions = pageTransaction.getContent().stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        return PagingTransactionResponse.of(pageTransaction, transactions);
    }

    @Transactional
    public void confirmTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TRANSACTION_NOT_FOUND));
        transaction.confirm();
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        TransactionType transactionType = transactionTypeRepository.findByName(request.enumName())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TRANSACTION_TYPE_NOT_FOUND));
        if (!transactionType.getGroupType().equals(TransactionGroup.ADJUSTMENT) && request.scheduledDate() == null) {
            throw new BusinessException(ErrorCode.SCHEDULED_DATE_REQUIRED);
        }
        if (transactionType.getGroupType().equals(TransactionGroup.ADJUSTMENT) && request.scheduledDate() != null) {
            throw new BusinessException(ErrorCode.SCHEDULED_DATE_NOT_ALLOWED);
        }
        Transaction transaction = request.toEntity(product, transactionType);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.from(savedTransaction);
    }
}
