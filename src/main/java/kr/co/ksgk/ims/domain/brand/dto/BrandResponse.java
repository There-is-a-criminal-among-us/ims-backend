package kr.co.ksgk.ims.domain.brand.dto;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.company.dto.CompanyResponse;
import lombok.Builder;

@Builder
public record BrandResponse(
        int id,
        String name,
        CompanyResponse company
) {
    public static BrandResponse from(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .company(CompanyResponse.from(brand.getCompany()))
                .build();
    }
}
