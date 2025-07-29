package kr.co.ksgk.ims.domain.ocr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.ocr.dto.request.OcrExtractRequest;
import kr.co.ksgk.ims.domain.ocr.dto.response.OcrExtractResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OCR", description = "OCR 관련 API")
public interface OcrApi {

    @Operation(
            summary = "OCR 송장 추출",
            description = """
                    OCR을 사용하여 인보이스 정보를 추출합니다. \s
                    Presigned URL을 통해 이미지를 업로드한 후 keyName을 요청에 포함해야 합니다. \s
                    응답의 invoiceImageUrl을 통해 송장 등록 시 같은 이미지를 첨부할 수 있습니다.
                    """
    )
    @ApiResponse(responseCode = "200", description = "OCR 송장 추출 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OcrExtractResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> extractInvoice(@RequestBody OcrExtractRequest request) throws JsonProcessingException;
}
