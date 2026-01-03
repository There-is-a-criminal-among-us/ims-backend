package kr.co.ksgk.ims.domain.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "출퇴근 시간 수정 요청")
public record AttendanceUpdateRequest(
        @Schema(description = "출근 시간", example = "2023-12-01T09:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @Schema(description = "퇴근 시간", example = "2023-12-01T18:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime
) {
}