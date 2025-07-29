package kr.co.ksgk.ims.domain.invoice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.ksgk.ims.domain.invoice.dto.request.InvoiceUpdateRequest;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadInvoiceInfoRequest;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.PagingInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.service.InvoiceService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController implements InvoiceApi {

    private final InvoiceService invoiceService;

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createInvoice(@RequestBody UploadInvoiceInfoRequest request) {
        SimpleInvoiceInfoResponse response = invoiceService.createInvoice(request);
        return SuccessResponse.created(response);
    }

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getInvoiceList(
            @Parameter(description = "고객명, 전화번호 검색어")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PagingInvoiceInfoResponse pagingInvoiceInfoResponse = invoiceService.getInvoiceList(search, PageRequest.of(page, size));
        return SuccessResponse.ok(pagingInvoiceInfoResponse);
    }

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<SuccessResponse<?>> getInvoice(@PathVariable Long invoiceId) {
        InvoiceInfoResponse invoiceInfoResponse = invoiceService.getInvoiceInfo(invoiceId);
        return SuccessResponse.ok(invoiceInfoResponse);
    }

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @PutMapping("/{invoiceId}")
    public ResponseEntity<SuccessResponse<?>> updateInvoiceInfo(@PathVariable Long invoiceId, @RequestBody InvoiceUpdateRequest request) {
        InvoiceInfoResponse invoiceInfoResponse = invoiceService.updateInvoiceInfo(invoiceId, request);
        return SuccessResponse.ok(invoiceInfoResponse);
    }

    @PreAuthorize("hasAnyRole('OCR', 'ADMIN')")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<SuccessResponse<?>> deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return SuccessResponse.noContent();
    }
}