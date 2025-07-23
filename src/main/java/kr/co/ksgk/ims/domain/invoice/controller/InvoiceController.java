package kr.co.ksgk.ims.domain.invoice.controller;

import kr.co.ksgk.ims.domain.invoice.dto.request.UploadedInfo;
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
}
