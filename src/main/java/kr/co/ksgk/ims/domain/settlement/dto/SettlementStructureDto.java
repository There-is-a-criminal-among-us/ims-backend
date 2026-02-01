package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.ChargeCategory;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementType;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SettlementStructureDto(
        List<SettlementTypeDto> types,
        List<ChargeCategoryDto> chargeCategories
) {
    public static SettlementStructureDto from(List<SettlementType> types, List<ChargeCategory> chargeCategories) {
        return SettlementStructureDto.builder()
                .types(types.stream()
                        .map(SettlementTypeDto::from)
                        .collect(Collectors.toList())
                )
                .chargeCategories(chargeCategories.stream()
                        .map(ChargeCategoryDto::from)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
