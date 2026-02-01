package kr.co.ksgk.ims.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;

import java.util.List;

public record ProductMappingRequest(
        @NotBlank
        String rawName,
        List<ProductMappingDetailsRequest> products,
        Long sizeUnitId,
        Long returnSizeUnitId,
        String coupangCode
) {
    public RawProduct toEntity() {
        return RawProduct.builder()
                .name(rawName)
                .build();
    }
}
