package kr.co.ksgk.ims.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberPasswordChangeRequestDto
{
    private String oldPassword;
    private String newPassword;
}
