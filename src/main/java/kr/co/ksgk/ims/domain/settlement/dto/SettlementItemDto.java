package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SettlementItemDto(
        Long id,
        String name,
        int displayOrder,
        List<SettlementUnitDto> units
) {
    public static SettlementItemDto from(SettlementItem item) {
        return SettlementItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .displayOrder(item.getDisplayOrder())
                .units(item.getUnits().stream()
                        .map(SettlementUnitDto::from)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
