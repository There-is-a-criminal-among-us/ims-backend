package kr.co.ksgk.ims.domain.payslip.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreatePayslipRequest(
        @NotNull Long memberId,
        @NotBlank @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "날짜 형식은 yyyy-MM이어야 합니다 (예: 2025-06)")
        String date,
        @NotBlank String s3Key
) {}
