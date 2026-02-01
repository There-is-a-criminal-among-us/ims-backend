package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ProductMappingResponse(
        long id,
        String rawName,
        List<ProductMappingDetailsResponse> mappings,
        Long sizeUnitId,
        String sizeUnitName,
        Long returnSizeUnitId,
        String returnSizeUnitName,
        String coupangCode
) {
    public static ProductMappingResponse from(List<ProductMapping> productMappings) {
        if (productMappings == null || productMappings.isEmpty()) {
            return ProductMappingResponse.builder()
                    .id(0L)
                    .rawName("")
                    .mappings(List.of())
                    .build();
        }
        RawProduct rawProduct = productMappings.get(0).getRawProduct();
        return ProductMappingResponse.builder()
                .id(rawProduct.getId())
                .rawName(rawProduct.getName())
                .mappings(productMappings.stream()
                        .map(ProductMappingDetailsResponse::from)
                        .collect(Collectors.toList()))
                .sizeUnitId(rawProduct.getSizeUnit() != null ? rawProduct.getSizeUnit().getId() : null)
                .sizeUnitName(rawProduct.getSizeUnit() != null ? rawProduct.getSizeUnit().getName() : null)
                .returnSizeUnitId(rawProduct.getReturnSizeUnit() != null ? rawProduct.getReturnSizeUnit().getId() : null)
                .returnSizeUnitName(rawProduct.getReturnSizeUnit() != null ? rawProduct.getReturnSizeUnit().getName() : null)
                .coupangCode(rawProduct.getCoupangCode())
                .build();
    }
}
