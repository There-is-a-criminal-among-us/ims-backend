package kr.co.ksgk.ims.domain.returns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.ksgk.ims.domain.returns.dto.request.AcceptReturnRequest;
import kr.co.ksgk.ims.domain.returns.dto.request.CreateReturnRequest;
import kr.co.ksgk.ims.domain.returns.dto.request.PatchReturnRequest;
import kr.co.ksgk.ims.domain.returns.dto.response.*;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import kr.co.ksgk.ims.domain.returns.exception.InvoiceValidationException;
import kr.co.ksgk.ims.domain.returns.service.ReturnService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/returns")
@Tag(name = "Return", description = "회수 관련 API")
public class ReturnController {

    private final ReturnService returnService;

    @Operation(
            summary = "회수 정보 등록",
            description = "회수 정보를 등록합니다"
    )
    @ApiResponse(responseCode = "201", description = "회수 정보 등록 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnResponse.class))
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createReturn(@RequestBody @Valid CreateReturnRequest request) {
        ReturnResponse response = returnService.createReturn(request);
        return SuccessResponse.created(response);
    }

    @Operation(
            summary = "회수 정보 목록 조회",
            description = "로그인한 사용자가 관리하는 브랜드들의 회수 정보 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "회수 정보 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingReturnListResponse.class))
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getReturnInfos(
            @Auth Long memberId,

            @Parameter(description = "검색어 (구매자, 수령인, 전화번호, 상품명, 운송장번호)")
            @RequestParam(required = false) String search,

            @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "회수 상태 (REQUESTED, IN_PROGRESS, COMPLETED)")
            @RequestParam(required = false) ReturnStatus status,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PagingReturnListResponse response = returnService.getReturnInfosByMember(
                memberId,
                search,
                startDate,
                endDate,
                status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return SuccessResponse.ok(response);
    }

    @Operation(
            summary = "회수 정보 상세 조회",
            description = "회수 정보를 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "회수 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnResponse.class))
    )
    @GetMapping("/{returnId}")
    public ResponseEntity<SuccessResponse<?>> getReturn(@Auth Long memberId, @PathVariable Long returnId) {
        returnService.validateReturnInfoAccess(memberId, returnId);
        ReturnResponse response = returnService.getReturn(returnId);
        return SuccessResponse.ok(response);
    }

    @Operation(
            summary = "회수 정보 수정",
            description = "회수 정보를 수정합니다. null이 아닌 필드만 업데이트됩니다."
    )
    @ApiResponse(responseCode = "200", description = "회수 정보 부분 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReturnResponse.class))
    )
    @PatchMapping("/{returnId}")
    public ResponseEntity<SuccessResponse<?>> patchReturn(
            @Auth Long memberId, @PathVariable Long returnId, @RequestBody PatchReturnRequest request) {
        ReturnResponse response = returnService.patchReturn(memberId, returnId, request);
        return SuccessResponse.ok(response);
    }

    @Operation(
            summary = "회수 접수",
            description = "회수 정보를 접수합니다. 회수 상태를 IN_PROGRESS로 변경하고 접수일을 등록합니다. 단일 또는 여러 건을 동시에 접수할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "회수 접수 성공")
    @PostMapping("/accept")
    public ResponseEntity<SuccessResponse<?>> acceptReturns(@Auth Long memberId, @RequestBody AcceptReturnRequest request) {
        returnService.acceptReturns(memberId, request.returnIds());
        return SuccessResponse.ok(null);
    }

    @Operation(
            summary = "반송장 엑셀 업로드",
            description = "엑셀 파일을 업로드하여 원송장에 해당하는 반송장을 일괄 등록합니다."
    )
    @ApiResponse(responseCode = "200", description = "반송장 업로드 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvoiceUploadSuccessResponse.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "엑셀 파일 유효성 검사 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvoiceUploadErrorResponse.class)
            )
    )
    @PostMapping(value = "/upload-invoices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadReturnInvoices(
            @Parameter(description = "업로드할 엑셀 파일", required = true)
            @RequestPart("files") List<MultipartFile> files
    ) {
        try {
            InvoiceUploadSuccessResponse response = returnService.uploadReturnInvoices(files);
            return SuccessResponse.ok(response);
        } catch (InvoiceValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrorResponse());
        }
    }

    @Operation(
            summary = "회수 정보 엑셀 업로드",
            description = "엑셀 파일을 업로드하여 회수 정보를 일괄 등록합니다."
    )
    @ApiResponse(responseCode = "200", description = "회수 정보 업로드 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ReturnExcelUploadResponse.class)
            )
    )
    @PostMapping(value = "/upload-returns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<?>> uploadReturnExcel(
            @Parameter(description = "업로드할 엑셀 파일", required = true)
            @RequestPart("files") List<MultipartFile> files
    ) {
        ReturnExcelUploadResponse response = returnService.uploadReturnExcel(files);
        return SuccessResponse.ok(response);
    }
}
