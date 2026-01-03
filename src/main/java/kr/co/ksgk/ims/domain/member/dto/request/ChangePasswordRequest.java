package kr.co.ksgk.ims.domain.member.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}