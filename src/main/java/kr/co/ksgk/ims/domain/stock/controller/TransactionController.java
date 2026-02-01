package kr.co.ksgk.ims.domain.stock.controller;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionRequest;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionUpdateRequest;
import kr.co.ksgk.ims.domain.stock.dto.response.PagingTransactionResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.TransactionResponse;
import kr.co.ksgk.ims.domain.stock.service.TransactionService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController implements TransactionApi {

    private final TransactionService transactionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'OCR', 'MEMBER')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllTransactions(
            @Auth Long memberId,
            @Parameter(description = "사업자, 브랜드, 품목명 검색어")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "검색 유형 (OUTGOING | INCOMING | NAVER_FULFILLMENT | COUPANG_FULFILLMENT | ADJUSTMENT)")
            @RequestParam(required = false) List<String> types,

            @Parameter(description = "검색 시작 날짜", example = "2025-01-01")
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(description = "검색 종료 날짜", example = "2025-12-31")
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingTransactionResponse pagingTransactionResponse = transactionService.getAllTransactions(memberId, search, types, startDate, endDate, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return SuccessResponse.ok(pagingTransactionResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{transactionId}/confirm")
    public ResponseEntity<SuccessResponse<?>> confirmTransaction(@PathVariable Long transactionId) {
        transactionService.confirmTransaction(transactionId);
        return SuccessResponse.noContent();
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.createTransaction(request);
        return SuccessResponse.created(transactionResponse);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<SuccessResponse<?>> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionUpdateRequest request) {
        TransactionResponse response = transactionService.updateTransaction(transactionId, request);
        return SuccessResponse.ok(response);
    }
}
