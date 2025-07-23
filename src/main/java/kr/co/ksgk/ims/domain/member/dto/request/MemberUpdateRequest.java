package kr.co.ksgk.ims.domain.member.dto.request;

import java.util.List;

public record MemberUpdateRequest(
        String name,
        String phone,
        String note,
        List<Long> managingCompanies,
        List<Long> managingBrands
) {
}
