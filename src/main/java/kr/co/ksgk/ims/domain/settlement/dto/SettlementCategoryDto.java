package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementCategory;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SettlementCategoryDto(
        Long id,
        String name,
        int displayOrder,
        List<SettlementItemDto> items
) {
    public static SettlementCategoryDto from(SettlementCategory category) {
        return SettlementCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .items(category.getItems().stream()
                        .map(SettlementItemDto::from)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
