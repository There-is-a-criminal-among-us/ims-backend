package kr.co.ksgk.ims.domain.attendance.dto.response;

import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.domain.auth.dto.response.MemberResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record AttendanceResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static AttendanceResponse from(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .member(MemberResponse.from(attendance.getMember()))
                .date(attendance.getDate())
                .startTime(attendance.getStartTime())
                .endTime(attendance.getEndTime())
                .build();
    }
}
