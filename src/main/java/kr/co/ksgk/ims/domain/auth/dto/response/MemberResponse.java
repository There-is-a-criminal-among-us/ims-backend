package kr.co.ksgk.ims.domain.auth.dto.response;

import kr.co.ksgk.ims.domain.member.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberResponse(
        long id,
        String username,
        String name,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
