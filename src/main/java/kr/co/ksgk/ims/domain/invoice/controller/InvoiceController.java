package kr.co.ksgk.ims.domain.invoice.controller;

import kr.co.ksgk.ims.domain.invoice.dto.*;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import  kr.co.ksgk.ims.domain.invoice.service.InvoiceService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController
{
    private final InvoiceService invoiceService;

    @PostMapping
    ResponseEntity<SuccessResponse<?>> uploadInvoice(@RequestBody InvoiceUploadRequestDto dto)
    {
        InvoiceUploadResponseDto response=invoiceService.uploadInvoice(dto);

        return SuccessResponse.ok(response);
    }
}
