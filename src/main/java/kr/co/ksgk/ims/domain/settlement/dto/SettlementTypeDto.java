package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.SettlementCategory;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementItem;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementType;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record SettlementTypeDto(
        Long id,
        String name,
        int displayOrder,
        List<SettlementCategoryDto> categories,
        List<SettlementItemDto> items
) {
    public static SettlementTypeDto from(SettlementType type) {
        return SettlementTypeDto.builder()
                .id(type.getId())
                .name(type.getName())
                .displayOrder(type.getDisplayOrder())
                .categories(type.getCategories().stream()
                        .sorted(Comparator.comparing(SettlementCategory::getDisplayOrder))
                        .map(SettlementCategoryDto::from)
                        .toList()
                )
                .items(type.getDirectItems().stream()
                        .sorted(Comparator.comparing(SettlementItem::getDisplayOrder))
                        .map(SettlementItemDto::from)
                        .toList()
                )
                .build();
    }
}
