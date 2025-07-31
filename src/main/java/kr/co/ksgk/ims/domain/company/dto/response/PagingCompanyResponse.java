package kr.co.ksgk.ims.domain.company.dto.response;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingCompanyResponse(
        PageResponse page,
        List<CompanyResponse> companies
) {
    public static PagingCompanyResponse of(Page<Company> pageCompany, List<CompanyResponse> companies) {
        return PagingCompanyResponse.builder()
                .page(PageResponse.from(pageCompany))
                .companies(companies)
                .build();
    }
}
