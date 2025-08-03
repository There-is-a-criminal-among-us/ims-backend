package kr.co.ksgk.ims.domain.company.service;

import kr.co.ksgk.ims.domain.brand.dto.BrandTreeResponse;
import kr.co.ksgk.ims.domain.company.dto.CompanyTreeResponse;
import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.dto.request.CompanyRequest;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import kr.co.ksgk.ims.domain.company.dto.response.PagingCompanyResponse;
import kr.co.ksgk.ims.domain.member.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;

    public TreeResponse getCompanyTree(Long memberId) {
        // Get member with their brand relationships
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<Company> companies;
        Set<Long> memberBrandIds;
        if (!member.getRole().equals(Role.ADMIN)) {
            // Get companies that the member manages
            companies = companyRepository.findCompaniesByMemberWithBrandsAndProducts(memberId);

            // Get brand IDs that the member manages
            memberBrandIds = member.getMemberBrands().stream()
                    .map(mb -> mb.getBrand().getId())
                    .collect(Collectors.toSet());
        } else {
            companies = companyRepository.findAllWithBrandsAndProducts();
            memberBrandIds = Set.of();
        }
        // Create filtered company tree responses
        List<CompanyTreeResponse> companyTreeResponseList = companies.stream()
                .map(company -> createFilteredCompanyTreeResponse(company, memberBrandIds))
                .collect(Collectors.toList());
        return TreeResponse.from(companyTreeResponseList);
    }

    private CompanyTreeResponse createFilteredCompanyTreeResponse(Company company, Set<Long> memberBrandIds) {
        return CompanyTreeResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .note(company.getNote())
                .brands(company.getBrands().stream()
                        .filter(brand -> memberBrandIds.contains(brand.getId()))
                        .map(BrandTreeResponse::from)
                        .toList())
                .build();
    }

    // 등록
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = request.toEntity();
        Company saved = companyRepository.save(company);
        return CompanyResponse.from(saved);
    }

    // 전체 조회
    public PagingCompanyResponse getAllCompanies(String search, Pageable pageable) {
        Page<Company> pageCompany;
        if (search == null || search.isBlank()) {
            pageCompany = companyRepository.findAll(pageable);
        } else {
            pageCompany = companyRepository.findByNameContaining(search, pageable);
        }
        List<CompanyResponse> companies = pageCompany.getContent().stream()
                .map(CompanyResponse::from)
                .collect(Collectors.toList());
        return PagingCompanyResponse.of(pageCompany, companies);
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
