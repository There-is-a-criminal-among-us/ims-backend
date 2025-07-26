package kr.co.ksgk.ims.domain.product.dto.request;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductRequest(
        Long id,
        Long brandId,
        String name,
        String note
) {
    public Product toEntity(Brand brand) {
        return Product.builder()
                .brand(brand)
                .name(this.name)
                .note(this.note)
                .build();
    }
}
