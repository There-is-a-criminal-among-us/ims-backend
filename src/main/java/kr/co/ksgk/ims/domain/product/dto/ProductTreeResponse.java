package kr.co.ksgk.ims.domain.product.dto;

import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductTreeResponse(
        int id,
        String name
) {
    public static ProductTreeResponse from(Product product) {
        return ProductTreeResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
    }
}
