package kr.co.ksgk.ims.domain.product.dto.request;

import java.util.List;

public record ProductMappingUpdateRequest(
        String rawName,
        List<ProductMappingDetailsRequest> products,
        Long sizeUnitId,
        Long returnSizeUnitId,
        String coupangCode
) {
}