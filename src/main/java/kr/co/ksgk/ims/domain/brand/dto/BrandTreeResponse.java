package kr.co.ksgk.ims.domain.brand.dto;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.dto.ProductTreeResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record BrandTreeResponse(
        long id,
        String name,
        String note,
        List<ProductTreeResponse> products
) {
    public static BrandTreeResponse from(Brand brand) {
        return BrandTreeResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .note(brand.getNote())
                .products(brand.getProducts().stream()
                        .map(ProductTreeResponse::from)
                        .toList())
                .build();
    }
}
