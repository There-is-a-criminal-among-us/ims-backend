package kr.co.ksgk.ims.domain.company.dto;

import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;

@Builder
public record CompanyResponse(
        int id,
        String name
) {
    public static CompanyResponse from(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }
}
