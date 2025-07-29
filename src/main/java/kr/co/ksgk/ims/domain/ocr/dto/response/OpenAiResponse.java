package kr.co.ksgk.ims.domain.ocr.dto.response;

import java.util.List;

public record OpenAiResponse(
        List<Choice> choices
) {
    public record Choice(
            Message message
    ) {
        public record Message(
                FunctionCall function_call
        ) {
            public record FunctionCall(
                    String name,
                    String arguments
            ) {
            }
        }
    }
}
