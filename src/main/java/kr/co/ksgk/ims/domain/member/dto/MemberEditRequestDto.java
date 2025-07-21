package kr.co.ksgk.ims.domain.member.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class MemberEditRequestDto
{
    private String name;
    private List<Long> managingCompanies;
    private List<Long> managingBrands;
    private String phone;
    private String note;
}
