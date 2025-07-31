package kr.co.ksgk.ims.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.stock.dto.request.TransactionRequest;
import kr.co.ksgk.ims.domain.stock.dto.response.PagingTransactionResponse;
import kr.co.ksgk.ims.domain.stock.dto.response.TransactionResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Transaction", description = "입출고 관련 API")
public interface TransactionApi {

    @Operation(
            summary = "입출고 내역 조회",
            description = "사업자, 브랜드, 품목명으로 입출고 내역을 검색합니다. " +
                    "검색 유형(type)을 지정하지 않으면 모든 유형의 거래가 조회됩니다. " +
                    "날짜 범위를 지정할 수 있으며, 페이지네이션을 지원합니다."
    )
    @ApiResponse(responseCode = "200", description = "입출고 내역 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingTransactionResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getAllTransactions(
            @Parameter(description = "사업자, 브랜드, 품목명 검색어")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "검색 유형 (OUTGOING | INCOMING | NAVER | COUPANG | ADJUSTMENT)")
            @RequestParam(required = false) List<String> types,

            @Parameter(description = "검색 시작 날짜", example = "2025-01-01")
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(description = "검색 종료 날짜", example = "2025-12-31")
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "입출고 내역 상세 조회",
            description = "입출고 내역의 상세 정보를 조회합니다. 입출고 ID를 사용하여 특정 거래를 조회합니다."
    )
    @ApiResponse(responseCode = "204", description = "입출고 내역 상세 조회 성공")
    ResponseEntity<SuccessResponse<?>> confirmTransaction(@PathVariable Long transactionId);

    @Operation(
            summary = "입출고 등록",
            description = """
                    새로운 입출고 내역을 생성합니다. 입출고 요청 정보를 포함한 요청 본문이 필요합니다. \s
                    enumName은 노션 입출고, 기타수량 ENUM 페이지의 영어 ENUM명을 추가하면 됩니다. \s
                    기타수량의 경우 scheduledDate를 허용하지 않습니다."""
    )
    @ApiResponse(responseCode = "201", description = "입출고 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> createTransaction(@RequestBody TransactionRequest request);
}
