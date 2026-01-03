package kr.co.ksgk.ims.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.auth.dto.request.LoginRequest;
import kr.co.ksgk.ims.domain.auth.dto.request.ReissueRequest;
import kr.co.ksgk.ims.domain.auth.dto.response.TokenResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "로그인 요청을 처리합니다. 성공 시 토큰을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> login(@RequestBody LoginRequest request);

    @PostMapping("/token")
    @Operation(
            summary = "토큰 재발급",
            description = "리프레쉬 토큰을 사용하여 새로운 토큰을 재발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "토큰 재발급",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> reissueToken(@RequestBody ReissueRequest request);
}
