package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String note

) {
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .note(product.getNote())
                .build();
    }
}