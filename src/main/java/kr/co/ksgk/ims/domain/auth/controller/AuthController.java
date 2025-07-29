package kr.co.ksgk.ims.domain.auth.controller;

import kr.co.ksgk.ims.domain.auth.dto.request.LoginRequest;
import kr.co.ksgk.ims.domain.auth.dto.request.ReissueRequest;
import kr.co.ksgk.ims.domain.auth.dto.request.SignupRequest;
import kr.co.ksgk.ims.domain.auth.dto.response.MemberResponse;
import kr.co.ksgk.ims.domain.auth.dto.response.TokenResponse;
import kr.co.ksgk.ims.domain.auth.service.AuthService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements  AuthApi {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<?>> signup(@RequestBody SignupRequest request) {
        MemberResponse memberResponse = authService.signup(request);
        return SuccessResponse.created(memberResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return SuccessResponse.ok(tokenResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<?>> reissueToken(@RequestBody ReissueRequest request) {
        TokenResponse tokenResponse = authService.reissueToken(request);
        return SuccessResponse.ok(tokenResponse);
    }
}