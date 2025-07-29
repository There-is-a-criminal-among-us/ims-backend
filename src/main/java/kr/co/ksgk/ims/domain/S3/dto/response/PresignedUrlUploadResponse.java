package kr.co.ksgk.ims.domain.S3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PresignedUrlUploadResponse(
        @Schema(description = "S3에 PUT 요청할 수 있는 Presigned URL")
        String url,
        @Schema(description = "S3 객체 key 값")
        String keyName
) {
    public static PresignedUrlUploadResponse of(String url, String keyName) {
        return PresignedUrlUploadResponse.builder()
                .url(url)
                .keyName(keyName)
                .build();
    }
}
