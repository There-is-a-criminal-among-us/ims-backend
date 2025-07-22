package kr.co.ksgk.ims.domain.member.dto.response;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingMemberInfoResponse(
        PageResponse page,
        List<MemberInfoResponse> members
) {
    public static PagingMemberInfoResponse of(Page<Member> pageMember, List<MemberInfoResponse> memberInfoResponses) {
        return PagingMemberInfoResponse.builder()
                .page(PageResponse.from(pageMember))
                .members(memberInfoResponses)
                .build();
    }
}
