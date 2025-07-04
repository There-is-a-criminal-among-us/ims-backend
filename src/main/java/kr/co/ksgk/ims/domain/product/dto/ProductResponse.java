package kr.co.ksgk.ims.domain.product.dto;

import kr.co.ksgk.ims.domain.brand.dto.BrandResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
        int id,
        String name,
        BrandResponse brand
) {
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(BrandResponse.from(product.getBrand()))
                .build();
    }
}
