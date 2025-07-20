package kr.co.ksgk.ims.domain.member.dto;

import kr.co.ksgk.ims.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MemberInfoResponseDto
{
    private long id;
    private String username;
    private String name;
    private String phone;
    private Role role;
    private List<SimpleInfoDto> managingCompanies;
    private List<SimpleInfoDto> managingBrands;
    private String note;
    private String createdAt;
    private String updatedAt;
}
