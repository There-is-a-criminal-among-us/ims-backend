package kr.co.ksgk.ims.domain.invoice.dto;

import lombok.Getter;

@Getter
public class ProductInfoRequestDto
{
    private Long productId;
    private Integer returnedQuantity;
    private Integer resaleableQuantity;
    private String note;
}
