package kr.co.ksgk.ims.domain.attendance.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceCreateRequest(
        @NotNull LocalDate date,
        @NotNull LocalDateTime startTime,
        LocalDateTime endTime
) {}
