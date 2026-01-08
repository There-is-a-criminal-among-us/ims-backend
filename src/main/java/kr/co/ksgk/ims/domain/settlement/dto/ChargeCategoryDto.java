package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.ChargeCategory;
import lombok.Builder;

@Builder
public record ChargeCategoryDto(
        Long id,
        String name,
        int displayOrder
) {
    public static ChargeCategoryDto from(ChargeCategory chargeCategory) {
        return ChargeCategoryDto.builder()
                .id(chargeCategory.getId())
                .name(chargeCategory.getName())
                .displayOrder(chargeCategory.getDisplayOrder())
                .build();
    }
}
