package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record SettlementItemDto(
        Long id,
        String name,
        int displayOrder,
        CalculationType calculationType,
        List<SettlementUnitDto> units
) {
    public static SettlementItemDto from(SettlementItem item) {
        return SettlementItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .displayOrder(item.getDisplayOrder())
                .calculationType(item.getCalculationType())
                .units(item.getUnits().stream()
                        .sorted(Comparator.comparing(SettlementUnit::getDisplayOrder))
                        .map(SettlementUnitDto::from)
                        .toList()
                )
                .build();
    }
}
