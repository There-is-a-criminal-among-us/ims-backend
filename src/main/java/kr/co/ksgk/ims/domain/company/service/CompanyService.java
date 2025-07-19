package kr.co.ksgk.ims.domain.company.service;

import kr.co.ksgk.ims.domain.company.dto.CompanyTreeResponse;
import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public TreeResponse getCompanyTree() {
        List<Company> companies = companyRepository.findAllWithBrandsAndProducts();
        List<CompanyTreeResponse> companyTreeResponseList = companies.stream()
                .map(CompanyTreeResponse::from)
                .collect(Collectors.toList());
        return TreeResponse.from(companyTreeResponseList);
    }
}
