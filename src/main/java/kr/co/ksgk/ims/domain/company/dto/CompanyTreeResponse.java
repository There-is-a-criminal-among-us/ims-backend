package kr.co.ksgk.ims.domain.company.dto;

import kr.co.ksgk.ims.domain.brand.dto.BrandTreeResponse;
import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;

import java.util.List;

@Builder
public record CompanyTreeResponse(
        long id,
        String name,
        String note,
        List<BrandTreeResponse> brands
) {
    public static CompanyTreeResponse from(Company company) {
        return CompanyTreeResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .note(company.getNote())
                .brands(company.getBrands().stream()
                        .map(BrandTreeResponse::from)
                        .toList())
                .build();
    }
}
