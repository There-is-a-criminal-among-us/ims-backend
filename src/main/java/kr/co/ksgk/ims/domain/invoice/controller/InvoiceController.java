package kr.co.ksgk.ims.domain.invoice.controller;

import kr.co.ksgk.ims.domain.invoice.dto.request.UploadedInfo;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfo;
import kr.co.ksgk.ims.domain.invoice.service.InvoiceService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    ResponseEntity<SuccessResponse<?>> uploadInvoice(@RequestBody UploadedInfo request) {
        InvoiceInfo response = invoiceService.uploadInvoice(request);

        return SuccessResponse.ok(response);
    }
}
