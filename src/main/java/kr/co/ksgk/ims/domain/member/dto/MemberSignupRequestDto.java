package kr.co.ksgk.ims.domain.member.dto;

import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Getter;

@Getter
public class MemberSignupRequestDto
{
    private String username;
    private String password;
    private String name;
    private String company_name;
    private String phone;
    private String note;
    private Role role;
}
