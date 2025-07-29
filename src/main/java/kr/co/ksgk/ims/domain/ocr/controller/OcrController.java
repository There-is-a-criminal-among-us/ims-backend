package kr.co.ksgk.ims.domain.ocr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.ksgk.ims.domain.ocr.dto.request.OcrExtractRequest;
import kr.co.ksgk.ims.domain.ocr.dto.response.OcrExtractResponse;
import kr.co.ksgk.ims.domain.ocr.service.OcrService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrController implements OcrApi {

    private final OcrService ocrService;

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @PostMapping("/extract")
    public ResponseEntity<SuccessResponse<?>> extractInvoice(@RequestBody OcrExtractRequest request) throws JsonProcessingException {
        OcrExtractResponse ocrExtractResponse = ocrService.extractInvoice(request);
        return SuccessResponse.ok(ocrExtractResponse);
    }
}
