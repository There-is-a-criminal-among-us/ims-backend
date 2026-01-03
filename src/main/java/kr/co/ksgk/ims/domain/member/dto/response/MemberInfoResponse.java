package kr.co.ksgk.ims.domain.member.dto.response;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record MemberInfoResponse(
        long id,
        String username,
        String name,
        String phone,
        Role role,
        List<ManagingDto> managingCompanies,
        List<ManagingDto> managingBrands,
        String note,
        LocalTime workStartTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .phone(member.getPhone())
                .role(member.getRole())
                .managingCompanies(member.getMemberCompanies().stream()
                        .map(ManagingDto::from)
                        .collect(Collectors.toList())
                )
                .managingBrands(member.getMemberBrands().stream()
                        .map(ManagingDto::from)
                        .collect(Collectors.toList())
                )
                .note(member.getNote())
                .workStartTime(member.getWorkStartTime())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
