package kr.co.ksgk.ims.domain.product.dto;

import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductTreeResponse(
        long id,
        String name,
        String note
) {
    public static ProductTreeResponse from(Product product) {
        return ProductTreeResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .note(product.getNote())
                .build();
    }
}
