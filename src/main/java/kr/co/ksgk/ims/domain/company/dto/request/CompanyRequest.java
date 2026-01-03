package kr.co.ksgk.ims.domain.company.dto.request;

import kr.co.ksgk.ims.domain.company.entity.Company;

public record CompanyRequest(
        String name,
        String businessNumber,
        String representativeName,
        String address,
        String note
) {
    public Company toEntity() {
        return Company.builder()
                .name(name)
                .businessNumber(businessNumber)
                .representativeName(representativeName)
                .address(address)
                .note(note)
                .build();
    }
}
