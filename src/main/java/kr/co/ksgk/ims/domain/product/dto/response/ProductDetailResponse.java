package kr.co.ksgk.ims.domain.product.dto.response;

import lombok.Builder;

@Builder
public record ProductDetailResponse(
        ProductResponse productResponse,
        ProductStatusResponse productStatusResponse
) {}
