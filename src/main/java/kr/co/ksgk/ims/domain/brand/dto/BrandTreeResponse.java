package kr.co.ksgk.ims.domain.brand.dto;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.dto.ProductTreeResponse;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record BrandTreeResponse(
        int id,
        String name,
        List<ProductTreeResponse> products
) {
    public static BrandTreeResponse from(Brand brand) {
        return BrandTreeResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .products(brand.getProducts().stream()
                        .map(ProductTreeResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
