package kr.co.ksgk.ims.domain.settlement.dto.response;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.Builder;

@Builder
public record SettlementUnitResponse(
        Long id,
        String name,
        int price,
        String itemName
) {
    public static SettlementUnitResponse from(SettlementUnit unit) {
        return SettlementUnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .price(unit.getPrice())
                .itemName(unit.getItem().getName())
                .build();
    }
}