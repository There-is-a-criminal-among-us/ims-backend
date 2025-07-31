package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String note,
        BrandResponse brand
) {
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .note(product.getNote())
                .brand(BrandResponse.from(product.getBrand()))
                .build();
    }
}