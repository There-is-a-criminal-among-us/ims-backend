package kr.co.ksgk.ims.domain.member.dto;

import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public class MemberLoginResponseDto
{
    private long id;
    private Role role;
}
