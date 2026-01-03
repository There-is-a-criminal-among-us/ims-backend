package kr.co.ksgk.ims.domain.S3.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 업로드 요청 DTO")
public record PresignedUrlUploadRequest(
        @Schema(description = "업로드할 파일 이름 (예: profile.jpg)")
        String fileName
) {
}
