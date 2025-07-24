package kr.co.ksgk.ims.domain.invoice.controller;

import kr.co.ksgk.ims.domain.invoice.dto.request.InvoiceUpdateRequest;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadedInfo;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceInfo;
import kr.co.ksgk.ims.domain.invoice.dto.response.PagingInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.service.InvoiceService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    ResponseEntity<SuccessResponse<?>> uploadInvoice(@RequestBody UploadedInfo request) {
        SimpleInvoiceInfo response = invoiceService.uploadInvoice(request);

        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    ResponseEntity<SuccessResponse<?>> getInvoice(@RequestParam(required = false) String search, Pageable pageable) {
        PagingInvoiceInfoResponse pagingInvoiceInfoResponse=invoiceService.getInvoiceList(search,pageable);

        return SuccessResponse.ok(pagingInvoiceInfoResponse);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{invoiceId}")
    ResponseEntity<SuccessResponse<?>> getInvoice(@PathVariable Long invoiceId) {
        InvoiceInfoResponse invoiceInfoResponse=invoiceService.getInvoiceInfo(invoiceId);

        return SuccessResponse.ok(invoiceInfoResponse);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{invoiceId}")
    ResponseEntity<SuccessResponse<?>> updateInvoiceInfo(@PathVariable Long invoiceId, @RequestBody InvoiceUpdateRequest request) {
        InvoiceInfoResponse invoiceInfoResponse=invoiceService.updateInvoiceInfo(invoiceId, request);

        return SuccessResponse.ok(invoiceInfoResponse);
    }
}
