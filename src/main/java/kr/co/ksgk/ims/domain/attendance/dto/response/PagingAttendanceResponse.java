package kr.co.ksgk.ims.domain.attendance.dto.response;

import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingAttendanceResponse(
        PageResponse page,
        List<AttendanceResponse> attendances
) {
    public static PagingAttendanceResponse of(Page<Attendance> pageAttendance, List<AttendanceResponse> attendanceResponses) {
        return PagingAttendanceResponse.builder()
                .page(PageResponse.from(pageAttendance))
                .attendances(attendanceResponses)
                .build();
    }
}
