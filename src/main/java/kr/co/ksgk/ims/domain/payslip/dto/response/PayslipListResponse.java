package kr.co.ksgk.ims.domain.payslip.dto.response;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.payslip.entity.Payslip;

import java.util.List;

public record PayslipListResponse(List<PayslipResponse> payslips) {
    public static PayslipListResponse from(List<Payslip> payslips, S3Service s3Service) {
        return new PayslipListResponse(
                payslips.stream()
                        .map(p -> PayslipResponse.from(p, s3Service))
                        .toList()
        );
    }
}
