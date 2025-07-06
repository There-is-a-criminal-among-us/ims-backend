package kr.co.ksgk.ims.domain.company.service;

import kr.co.ksgk.ims.domain.company.dto.CompanyResponse;
import kr.co.ksgk.ims.domain.company.dto.CompanyTreeResponse;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(CompanyResponse::from)
                .toList();
    }

    public CompanyResponse getCompanyById(int companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        return CompanyResponse.from(company);
    }

    public List<CompanyTreeResponse> getCompanyTree() {
        Set<Company> companies = companyRepository.findAllWithBrandsAndProducts();
        return companies.stream()
                .map(CompanyTreeResponse::from)
                .collect(Collectors.toList());
    }
}
