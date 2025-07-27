package kr.co.ksgk.ims.domain.member.dto.response;

import kr.co.ksgk.ims.domain.member.entity.MemberBrand;
import kr.co.ksgk.ims.domain.member.entity.MemberCompany;
import lombok.Builder;

@Builder
public record ManagingDto(
        long id,
        String name
) {
    public static ManagingDto from(MemberCompany memberCompany) {
        return ManagingDto
                .builder()
                .id(memberCompany.getCompany().getId())
                .name(memberCompany.getCompany().getName())
                .build();
    }

    public static ManagingDto from(MemberBrand memberBrand) {
        return ManagingDto
                .builder()
                .id(memberBrand.getBrand().getId())
                .name(memberBrand.getBrand().getName())
                .build();
    }
}
