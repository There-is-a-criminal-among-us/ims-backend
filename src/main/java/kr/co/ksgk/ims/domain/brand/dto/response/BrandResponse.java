package kr.co.ksgk.ims.domain.brand.dto.response;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import lombok.Builder;


@Builder
public record BrandResponse(
        long id,
        String name,
        String note,
        CompanyResponse company
) {
    public static BrandResponse from(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .note(brand.getNote())
                .company(CompanyResponse.from(brand.getCompany()))
                .build();
    }
}