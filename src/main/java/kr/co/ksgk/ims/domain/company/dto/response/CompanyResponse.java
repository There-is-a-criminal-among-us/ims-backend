package kr.co.ksgk.ims.domain.company.dto.response;

import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;


@Builder
public record CompanyResponse(
        long id,
        String name,
        String businessNumber,
        String representativeName,
        String address,
        String note
) {
    public static CompanyResponse from(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .businessNumber(company.getBusinessNumber())
                .representativeName(company.getRepresentativeName())
                .address(company.getAddress())
                .note(company.getNote())
                .build();
    }
}
