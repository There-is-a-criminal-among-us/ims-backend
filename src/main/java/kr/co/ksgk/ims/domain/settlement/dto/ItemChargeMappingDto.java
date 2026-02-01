package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.CompanyItemChargeMapping;
import lombok.Builder;

@Builder
public record ItemChargeMappingDto(
        Long id,
        Long settlementItemId,
        String settlementItemName,
        Long chargeCategoryId,
        String chargeCategoryName
) {
    public static ItemChargeMappingDto from(CompanyItemChargeMapping mapping) {
        return ItemChargeMappingDto.builder()
                .id(mapping.getId())
                .settlementItemId(mapping.getSettlementItem().getId())
                .settlementItemName(mapping.getSettlementItem().getName())
                .chargeCategoryId(mapping.getChargeCategory().getId())
                .chargeCategoryName(mapping.getChargeCategory().getName())
                .build();
    }
}