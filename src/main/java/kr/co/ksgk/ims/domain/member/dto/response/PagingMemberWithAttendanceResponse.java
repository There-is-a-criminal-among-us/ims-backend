package kr.co.ksgk.ims.domain.member.dto.response;

import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingMemberWithAttendanceResponse(
        PageResponse page,
        List<MemberWithAttendanceResponse> members
) {
    public static PagingMemberWithAttendanceResponse of(Page<Attendance> pageAttendance, List<MemberWithAttendanceResponse> memberWithAttendanceResponses) {
        return PagingMemberWithAttendanceResponse.builder()
                .page(PageResponse.from(pageAttendance))
                .members(memberWithAttendanceResponses)
                .build();
    }
}