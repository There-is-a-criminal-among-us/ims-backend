package kr.co.ksgk.ims.domain.settlement.dto.request;

import java.util.List;

public record UpdateCompanyChargeMappingRequest(
        List<ItemMappingRequest> mappings
) {
    public record ItemMappingRequest(
            Long settlementItemId,
            Long chargeCategoryId
    ) {
    }
}