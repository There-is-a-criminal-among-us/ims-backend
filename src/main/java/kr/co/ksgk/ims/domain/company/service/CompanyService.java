package kr.co.ksgk.ims.domain.company.service;

import kr.co.ksgk.ims.domain.company.dto.CompanyTreeResponse;
import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.dto.request.CompanyRequest;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import org.springframework.transaction.annotation.Transactional;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 등록
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = request.toEntity();
        Company saved = companyRepository.save(company);
        return CompanyResponse.from(saved);
    }

    // 조회
    public CompanyResponse getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        return CompanyResponse.from(company);
    }

    // 수정
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        if (request.name() != null) company.updateName(request.name());
        if (request.businessNumber() != null) company.updateBusinessNumber(request.businessNumber());
        if (request.representativeName() != null) company.updateRepresentativeName(request.representativeName());
        if (request.address() != null) company.updateAddress(request.address());
        if (request.note() != null) company.updateNote(request.note());
        return CompanyResponse.from(company);
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        companyRepository.delete(company);
    }
}
