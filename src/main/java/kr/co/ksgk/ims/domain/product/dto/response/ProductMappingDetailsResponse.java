package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import lombok.Builder;

@Builder
public record ProductMappingDetailsResponse(
        long id,
        long productId,
        String productName,
        int quantity
) {
    public static ProductMappingDetailsResponse from(ProductMapping productMapping) {
        return ProductMappingDetailsResponse.builder()
                .id(productMapping.getId())
                .productId(productMapping.getProduct().getId())
                .productName(productMapping.getProduct().getName())
                .quantity(productMapping.getQuantity())
                .build();
    }
}
