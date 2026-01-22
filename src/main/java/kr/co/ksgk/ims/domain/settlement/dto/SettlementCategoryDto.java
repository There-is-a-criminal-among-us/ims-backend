package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementCategory;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

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
                        .sorted(Comparator.comparing(SettlementItem::getDisplayOrder))
                        .map(SettlementItemDto::from)
                        .toList()
                )
                .build();
    }
}
