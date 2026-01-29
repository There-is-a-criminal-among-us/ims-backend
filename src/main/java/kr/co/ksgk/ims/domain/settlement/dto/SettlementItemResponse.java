package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record SettlementItemResponse(
        Long id,
        String name,
        CalculationType calculationType,
        List<UnitResponse> units
) {
    @Builder
    public record UnitResponse(Long id, String name, int price) {
        public static UnitResponse from(SettlementUnit unit) {
            return UnitResponse.builder()
                    .id(unit.getId())
                    .name(unit.getName())
                    .price(unit.getPrice())
                    .build();
        }
    }

    public static SettlementItemResponse from(SettlementItem item) {
        List<UnitResponse> unitResponses = item.getUnits().stream()
                .sorted(Comparator.comparing(SettlementUnit::getDisplayOrder))
                .map(UnitResponse::from)
                .toList();

        return SettlementItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .calculationType(item.getCalculationType())
                .units(unitResponses)
                .build();
    }
}
