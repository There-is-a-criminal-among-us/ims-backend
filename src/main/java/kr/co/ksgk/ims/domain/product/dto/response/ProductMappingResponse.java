package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ProductMappingResponse(
        long id,
        String rawName,
        List<ProductMappingDetailsResponse> mappings
) {
    public static ProductMappingResponse from(List<ProductMapping> productMappings) {
        if (productMappings == null || productMappings.isEmpty()) {
            return ProductMappingResponse.builder()
                    .id(0L)
                    .rawName("")
                    .mappings(List.of())
                    .build();
        }
        return ProductMappingResponse.builder()
                .id(productMappings.get(0).getRawProduct().getId())
                .rawName(productMappings.get(0).getRawProduct().getName())
                .mappings(productMappings.stream()
                        .map(ProductMappingDetailsResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
