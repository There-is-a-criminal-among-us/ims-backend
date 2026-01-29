package kr.co.ksgk.ims.domain.stock.service;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementItemRepository;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementUnitRepository;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionRequest;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionUpdateRequest;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionWorkRequest;
import kr.co.ksgk.ims.domain.stock.dto.response.PagingTransactionResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.TransactionResponse;
import kr.co.ksgk.ims.domain.stock.entity.*;
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
    private final SettlementItemRepository settlementItemRepository;
    private final SettlementUnitRepository settlementUnitRepository;
    private final StockLotService stockLotService;

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

        // 입고 확정 시 StockLot 생성
        handleStockLotOnConfirm(transaction);

        // 캐시 무효화: Transaction 상태 변경 시
        cacheInvalidator.invalidateCacheForProductIfToday(transaction.getProduct().getId());
    }

    /**
     * 트랜잭션 확정 시 StockLot 처리
     */
    private void handleStockLotOnConfirm(Transaction transaction) {
        TransactionGroup groupType = transaction.getTransactionType().getGroupType();
        LocalDate workDate = transaction.getWorkDate() != null ? transaction.getWorkDate() : LocalDate.now();

        if (groupType == TransactionGroup.INCOMING) {
            // 입고 확정 시 StockLot 생성
            StockLot stockLot = stockLotService.createLot(
                    transaction.getProduct(),
                    transaction,
                    workDate,
                    transaction.getQuantity()
            );
            transaction.updateStockLot(stockLot);
        } else if (groupType == TransactionGroup.OUTGOING) {
            // 출고 확정 시 FIFO 차감
            stockLotService.deductFifo(transaction.getProduct(), transaction.getQuantity());
        }
    }

    @Transactional
    public TransactionResponse updateTransaction(Long transactionId, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (request.quantity() != null) transaction.updateQuantity(request.quantity());
        if (request.note() != null) transaction.updateNote(request.note());
        if (request.scheduledDate() != null) transaction.updateScheduledDate(request.scheduledDate());
        if (request.workDate() != null) transaction.updateWorkDate(request.workDate());

        if (request.works() != null) {
            List<TransactionWork> works = createTransactionWorks(transaction, request.works());
            transaction.updateWorks(works);
        }

        cacheInvalidator.invalidateCacheForProductIfToday(transaction.getProduct().getId());

        return TransactionResponse.from(transaction);
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

        // TransactionWork 생성
        if (request.works() != null && !request.works().isEmpty()) {
            List<TransactionWork> works = createTransactionWorks(savedTransaction, request.works());
            savedTransaction.updateWorks(works);
        }

        // 캐시 무효화: 새로운 Transaction 생성 시
        cacheInvalidator.invalidateCacheForProductIfToday(product.getId());

        return TransactionResponse.from(savedTransaction);
    }

    private List<TransactionWork> createTransactionWorks(Transaction transaction, List<TransactionWorkRequest> workRequests) {
        return workRequests.stream()
                .map(workRequest -> {
                    SettlementItem item = settlementItemRepository.findById(workRequest.settlementItemId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_ITEM_NOT_FOUND));

                    if (workRequest.settlementUnitId() != null) {
                        // 작업종류 (Unit 있음 - 가격 자동계산)
                        SettlementUnit unit = settlementUnitRepository.findById(workRequest.settlementUnitId())
                                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_UNIT_NOT_FOUND));
                        return TransactionWork.createWithUnit(transaction, item, unit, workRequest.quantity());
                    } else {
                        // 용차구분 (Unit 없음 - 비용 직접 입력)
                        return TransactionWork.createWithoutUnit(transaction, item, workRequest.cost());
                    }
                })
                .toList();
    }
}
