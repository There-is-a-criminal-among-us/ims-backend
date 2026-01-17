package kr.co.ksgk.ims.domain.settlement.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CompanyChargeMappingDto(
        Long companyId,
        String companyName,
        List<ItemChargeMappingDto> mappings
) {
}