package kr.co.ksgk.ims.domain.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvoiceUploadResponseDto
{
    private Long invoiceId;
    private String number;
    private String name;
    private String phone;
    private String createdAt;
}
