package kr.co.ksgk.ims.domain.member.dto.response;

import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record MemberWithAttendanceResponse(
        long id,
        String username,
        String name,
        String phone,
        Role role,
        List<ManagingDto> managingCompanies,
        List<ManagingDto> managingBrands,
        String note,
        LocalTime workStartTime,
        LocalDateTime todayStartTime,
        LocalDateTime todayEndTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemberWithAttendanceResponse from(Attendance attendance) {
        var member = attendance.getMember();
        return MemberWithAttendanceResponse.builder()
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
                .todayStartTime(attendance.getStartTime())
                .todayEndTime(attendance.getEndTime())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}