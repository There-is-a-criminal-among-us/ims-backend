package kr.co.ksgk.ims.domain.ocr.dto.request;

import lombok.Builder;

@Builder
public record OpenAiChatMessage(
        String role,
        String content
) {
}
