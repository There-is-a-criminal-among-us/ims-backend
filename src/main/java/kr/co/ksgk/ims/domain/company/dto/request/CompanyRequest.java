package kr.co.ksgk.ims.domain.company.dto.request;

import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;

@Builder
public record CompanyRequest(
        Long id,
        String name,
        String businessNumber,
        String representativeName,
        String address,
        String note
) {
    public Company toEntity() {
        return Company.builder()
                .name(this.name)
                .businessNumber(this.businessNumber)
                .representativeName(this.representativeName)
                .address(this.address)
                .note(this.note)
                .build();


    }
}
