package kr.co.ksgk.ims.domain.company.dto;

import kr.co.ksgk.ims.domain.brand.dto.BrandTreeResponse;
import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CompanyTreeResponse(
        int id,
        String name,
        List<BrandTreeResponse> brands
) {
    public static CompanyTreeResponse from(Company company) {
        return CompanyTreeResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .brands(company.getBrands().stream()
                        .map(BrandTreeResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
