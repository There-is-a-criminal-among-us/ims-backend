package kr.co.ksgk.ims.domain.payslip.dto.response;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.payslip.entity.Payslip;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PayslipResponse(
        Long id,
        Long memberId,
        String memberName,
        String date,
        String fileUrl,
        LocalDateTime updatedAt
) {
    public static PayslipResponse from(Payslip payslip, S3Service s3Service) {
        return PayslipResponse.builder()
                .id(payslip.getId())
                .memberId(payslip.getMember().getId())
                .memberName(payslip.getMember().getName())
                .date(payslip.getDate())
                .fileUrl(s3Service.generatePresignedDownloadUrl(payslip.getS3Key()))
                .updatedAt(payslip.getUpdatedAt())
                .build();
    }
}
