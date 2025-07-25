package kr.co.ksgk.ims.domain.company.dto;

import java.util.List;

public record TreeResponse(
        List<CompanyTreeResponse> companies
) {
    public static TreeResponse from(List<CompanyTreeResponse> companies) {
        return new TreeResponse(companies);
    }
}
