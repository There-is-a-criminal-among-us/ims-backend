package kr.co.ksgk.ims.domain.invoice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.invoice.dto.request.InvoiceUpdateRequest;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadInvoiceInfoRequest;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.PagingInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceInfoResponse;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Invoice", description = "송장 API")
public interface InvoiceApi {

    @Operation(
            summary = "송장 등록",
            description = "송장을 등록합니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "201", description = "송장 등록 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleInvoiceInfoResponse.class)
            )
    )
    ResponseEntity<SuccessResponse<?>> createInvoice(@RequestBody UploadInvoiceInfoRequest request);

    @Operation(
            summary = "송장 목록 조회",
            description = "송장 목록을 조회합니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "송장 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagingInvoiceInfoResponse.class)
            )
    )
    ResponseEntity<SuccessResponse<?>> getInvoiceList(
            @Auth Long memberId,
            @Parameter(description = "사업자, 브랜드, 품목명 검색어")
            @RequestParam(defaultValue = "") String search,
            @Parameter(description = "년도 (예: 2024)")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "월 (1-12)")
            @RequestParam(required = false) Integer month,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size);

    @Operation(
            summary = "송장 전체 목록 조회 (페이징 없음)",
            description = "송장 전체 목록을 조회합니다. 페이징 처리가 없습니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "송장 전체 목록 조회 성공")
    ResponseEntity<SuccessResponse<?>> getInvoiceListAll(
            @Auth Long memberId,
            @Parameter(description = "사업자, 브랜드, 품목명 검색어")
            @RequestParam(defaultValue = "") String search,
            @Parameter(description = "년도 (예: 2024)")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "월 (1-12)")
            @RequestParam(required = false) Integer month);

    @Operation(
            summary = "송장 상세 조회",
            description = "송장 상세 정보를 조회합니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "송장 상세 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvoiceInfoResponse.class)
            )
    )
    ResponseEntity<SuccessResponse<?>> getInvoice(@PathVariable Long invoiceId);

    @Operation(
            summary = "송장 정보 수정",
            description = "송장 정보를 수정합니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "송장 정보 수정 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvoiceInfoResponse.class)
            )
    )
    ResponseEntity<SuccessResponse<?>> updateInvoiceInfo(@PathVariable Long invoiceId, @RequestBody InvoiceUpdateRequest request);

    @Operation(
            summary = "송장 삭제",
            description = "송장을 삭제합니다.  \n" +
                    "OCR 관리자 권한 이상이 필요합니다."
    )
    @ApiResponse(responseCode = "204", description = "송장 삭제 성공")
    ResponseEntity<SuccessResponse<?>> deleteInvoice(@PathVariable Long invoiceId);
}
