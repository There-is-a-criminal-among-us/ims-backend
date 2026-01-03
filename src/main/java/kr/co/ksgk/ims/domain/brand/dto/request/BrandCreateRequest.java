package kr.co.ksgk.ims.domain.brand.dto.request;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.company.entity.Company;

public record BrandCreateRequest(
        Long companyId,
        String name,
        String note
) {
    public Brand toEntity(Company company) {
        return Brand.builder()
                .company(company)
                .name(name)
                .note(note)
                .build();
    }
}
