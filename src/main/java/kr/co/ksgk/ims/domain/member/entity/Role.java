package kr.co.ksgk.ims.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("관리자"),
    OCR("OCR 담당자"),
    MEMBER("회원");

    private final String description;
}
