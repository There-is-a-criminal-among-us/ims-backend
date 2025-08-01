package kr.co.ksgk.ims.domain.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.delivery.dto.response.ExcelUploadErrorResponse;
import kr.co.ksgk.ims.domain.delivery.dto.response.ExcelUploadSuccessResponse;
import kr.co.ksgk.ims.domain.delivery.dto.response.PagingDeliveryResponse;
import kr.co.ksgk.ims.domain.delivery.exception.ExcelValidationException;
import kr.co.ksgk.ims.domain.delivery.service.DeliveryService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
@Tag(name = "Delivery", description = "배송 관련 API")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllDeliveries(
            @Parameter(description = "사업자, 브랜드, 상품명 검색어")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "검색 시작 날짜", example = "2025-01-01")
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(description = "검색 종료 날짜", example = "2025-12-31")
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingDeliveryResponse response = deliveryService.getAllDeliveries(search, startDate, endDate, PageRequest.of(page, size));
        return SuccessResponse.ok(response);
    }

    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "엑셀 파일 업로드", description = "여러 엑셀 파일을 업로드하여 배송 정보를 일괄 등록합니다.")
    @ApiResponse(responseCode = "200", description = "엑셀 파일 업로드 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExcelUploadSuccessResponse.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "엑셀 파일 유효성 검사 실패",
    content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExcelUploadErrorResponse.class)
            )
    )

    public ResponseEntity<?> uploadExcelFiles(
            @Parameter(description = "업로드할 엑셀 파일들", required = true)
            @RequestPart("files") List<MultipartFile> files
    ) {
        try {
            ExcelUploadSuccessResponse response = deliveryService.uploadExcelFiles(files);
            return SuccessResponse.ok(response);
        } catch (ExcelValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrorResponse());
        }
    }
}