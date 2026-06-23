package kr.co.ksgk.ims.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProductMoveRequest(
        @NotNull(message = "브랜드 ID는 필수입니다.")
        Long brandId
) {}
