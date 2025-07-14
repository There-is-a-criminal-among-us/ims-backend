package kr.co.ksgk.ims.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberSignupRequestDto
{
    private String username;
    private String password;
    private String name;
    private String company_name;
    private String phone;
    private String note;

}
