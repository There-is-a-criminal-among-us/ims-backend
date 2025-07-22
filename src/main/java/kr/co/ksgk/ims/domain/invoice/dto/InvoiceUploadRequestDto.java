package kr.co.ksgk.ims.domain.invoice.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class InvoiceUploadRequestDto
{
    private Long companyId;
    private String name;
    private String phone;
    private String number;
    private String invoiceImageUrl;
    private String productImageUrl;
    private List<ProductInfoRequestDto> products;
}
