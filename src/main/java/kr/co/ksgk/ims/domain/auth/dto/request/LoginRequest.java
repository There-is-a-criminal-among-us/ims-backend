package kr.co.ksgk.ims.domain.auth.dto.request;

public record LoginRequest(
    String username,
    String password
) {
}
