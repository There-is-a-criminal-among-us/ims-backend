package kr.co.ksgk.ims.domain.auth.dto;

import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Builder;

@Builder
public record MemberDto(
        long memberId,
        Role role
) {
    public static MemberDto of(long memberId, Role role) {
        return MemberDto.builder()
                .memberId(memberId)
                .role(role)
                .build();
    }
}
