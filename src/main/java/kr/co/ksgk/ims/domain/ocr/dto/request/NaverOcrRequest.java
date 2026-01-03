package kr.co.ksgk.ims.domain.ocr.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record NaverOcrRequest(
        String version,
        String requestId,
        long timestamp,
        List<ImageInfo> images
) {
    @Builder
    public record ImageInfo(
            String format,
            String name,
            String url
    ) {
    }
}
