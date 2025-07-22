package kr.co.ksgk.ims.domain.auth.dto;

import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Builder;

@Builder
public record AuthDto(
        long memberId,
        Role role
) {
    public static AuthDto of(long memberId, Role role) {
        return AuthDto.builder()
                .memberId(memberId)
                .role(role)
                .build();
    }
}
