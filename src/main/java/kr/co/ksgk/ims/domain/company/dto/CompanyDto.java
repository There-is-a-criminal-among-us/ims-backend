package kr.co.ksgk.ims.domain.company.dto;

import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDto {
    private Long id;
    private String name;
    private String businessNumber;
    private String representativeName;
    private String address;
    private String note;

    public static CompanyDto fromEntity(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .businessNumber(company.getBusinessNumber())
                .representativeName(company.getRepresentativeName())
                .address(company.getAddress())
                .note(company.getNote())
                .build();
    }
}