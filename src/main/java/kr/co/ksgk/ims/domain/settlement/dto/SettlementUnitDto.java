package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.Builder;

@Builder
public record SettlementUnitDto(
        Long id,
        String name,
        int price,
        int displayOrder
) {
    public static SettlementUnitDto from(SettlementUnit unit) {
        return SettlementUnitDto.builder()
                .id(unit.getId())
                .name(unit.getName())
                .price(unit.getPrice())
                .displayOrder(unit.getDisplayOrder())
                .build();
    }
}
