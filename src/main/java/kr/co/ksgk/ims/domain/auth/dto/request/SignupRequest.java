package kr.co.ksgk.ims.domain.auth.dto.request;

import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

public record SignupRequest(
        String username,
        String password,
        String name,
        String phone,
        String note,
        Role role
) {
    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phone(phone)
                .note(note)
                .role(role)
                .build();
    }
}
