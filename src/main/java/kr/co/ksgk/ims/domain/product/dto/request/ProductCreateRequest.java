package kr.co.ksgk.ims.domain.product.dto.request;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.product.entity.Product;

public record ProductCreateRequest(
        Long brandId,
        String name,
        String note
) {
    public Product toEntity(Brand brand) {
        return Product.builder()
                .brand(brand)
                .name(name)
                .note(note)
                .build();
    }
}
