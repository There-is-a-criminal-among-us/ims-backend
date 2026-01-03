package kr.co.ksgk.ims.domain.returns.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReturnPartyRequest(
        @NotBlank
        String name
) {
}