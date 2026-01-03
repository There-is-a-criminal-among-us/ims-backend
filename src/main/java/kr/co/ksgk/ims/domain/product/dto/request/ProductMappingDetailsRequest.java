package kr.co.ksgk.ims.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;

public record ProductMappingDetailsRequest(
        @NotNull
        long productId,
        @NotNull
        int quantity
) {
    public ProductMapping toEntity(Product product, RawProduct rawProduct) {
        return ProductMapping.builder()
                .product(product)
                .rawProduct(rawProduct)
                .quantity(quantity)
                .build();
    }
}
