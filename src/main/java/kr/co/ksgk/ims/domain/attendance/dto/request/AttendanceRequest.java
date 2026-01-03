package kr.co.ksgk.ims.domain.attendance.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AttendanceRequest(
        @NotBlank
        String token
) {
}
