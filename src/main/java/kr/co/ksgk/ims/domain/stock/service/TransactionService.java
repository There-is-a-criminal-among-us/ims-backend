package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionRequest;
import kr.co.ksgk.ims.domain.stock.dto.response.PagingTransactionResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.TransactionResponse;
import kr.co.ksgk.ims.domain.stock.entity.Transaction;
import kr.co.ksgk.ims.domain.stock.entity.TransactionGroup;
import kr.co.ksgk.ims.domain.stock.entity.TransactionStatus;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final StockCacheInvalidator cacheInvalidator;

    public PagingTransactionResponse getAllTransactions(
            Long memberId, String search, List<String> types, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Transaction> pageTransaction;
        if (member.getRole().equals(Role.ADMIN) || member.getRole().equals(Role.OCR)) {
            // ADMIN and OCR can see all transactions
            pageTransaction = transactionRepository.searchTransactions(search, types, startDate, endDate, pageable);
        } else {
            // MEMBER can only see transactions from their managed brands/companies
            Set<Long> allowedProductIds = getAllowedProductIds(member);
            pageTransaction = transactionRepository.searchTransactionsByProductIds(search, types, startDate, endDate, allowedProductIds, pageable);
        }
        
        List<TransactionResponse> transactions = pageTransaction.getContent().stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        return PagingTransactionResponse.of(pageTransaction, transactions);
    }
    
    private Set<Long> getAllowedProductIds(Member member) {
        Set<Long> memberBrandIds = member.getMemberBrands().stream()
                .map(mb -> mb.getBrand().getId())
                .collect(Collectors.toSet());
        
        if (!memberBrandIds.isEmpty()) {
            // If member has brand permissions, get products from those brands
            return productRepository.findIdsByBrandIdIn(memberBrandIds);
        } else {
            // If member has company permissions, get products from those companies
            Set<Long> memberCompanyIds = member.getMemberCompanies().stream()
                    .map(mc -> mc.getCompany().getId())
                    .collect(Collectors.toSet());
            return productRepository.findIdsByCompanyIdIn(memberCompanyIds);
        }
    }

    @Transactional
    public void confirmTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TRANSACTION_NOT_FOUND));
        transaction.confirm();
        
        // 캐시 무효화: Transaction 상태 변경 시
        cacheInvalidator.invalidateCacheForProductIfToday(transaction.getProduct().getId());
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        TransactionType transactionType = transactionTypeRepository.findByName(request.type())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TRANSACTION_TYPE_NOT_FOUND));
        if (!transactionType.getGroupType().equals(TransactionGroup.ADJUSTMENT) && request.scheduledDate() == null) {
            throw new BusinessException(ErrorCode.SCHEDULED_DATE_REQUIRED);
        }
        if (transactionType.getGroupType().equals(TransactionGroup.ADJUSTMENT) && request.scheduledDate() != null) {
            throw new BusinessException(ErrorCode.SCHEDULED_DATE_NOT_ALLOWED);
        }
        TransactionStatus transactionStatus = transactionType.getGroupType().equals(TransactionGroup.ADJUSTMENT)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.PENDING;
        Transaction transaction = request.toEntity(product, transactionType, transactionStatus);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // 캐시 무효화: 새로운 Transaction 생성 시
        cacheInvalidator.invalidateCacheForProductIfToday(product.getId());
        
        return TransactionResponse.from(savedTransaction);
    }
}
