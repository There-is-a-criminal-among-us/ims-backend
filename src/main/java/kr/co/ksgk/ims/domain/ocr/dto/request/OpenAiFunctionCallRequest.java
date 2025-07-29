package kr.co.ksgk.ims.domain.ocr.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record OpenAiFunctionCallRequest(
        String model,
        double temperature,
        List<OpenAiChatMessage> messages,
        List<FunctionDef> functions,
        Map<String, String> function_call
) {
    @Builder
    public record FunctionDef(
            String name,
            String description,
            Map<String, Object> parameters
    ) {
    }
}
