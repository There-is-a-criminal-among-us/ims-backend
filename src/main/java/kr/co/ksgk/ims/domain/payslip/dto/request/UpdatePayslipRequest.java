package kr.co.ksgk.ims.domain.payslip.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePayslipRequest(
        @NotNull Long memberId,
        @NotBlank String s3Key
) {}
