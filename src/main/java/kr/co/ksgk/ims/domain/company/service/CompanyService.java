package kr.co.ksgk.ims.domain.company.service;

import kr.co.ksgk.ims.domain.company.dto.CompanyDto;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    // 등록
    public CompanyDto createCompany(CompanyDto dto) {
        Company company = Company.builder()
                .name(dto.getName())
                .businessNumber(dto.getBusinessNumber())
                .representativeName(dto.getRepresentativeName())
                .address(dto.getAddress())
                .note(dto.getNote())
                .build();

        Company saved = companyRepository.save(company);
        return CompanyDto.fromEntity(saved);
    }

    // 조회
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(CompanyDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 수정
    public CompanyDto updateCompany(Long id, CompanyDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보가 없습니다. ID: " + id));

        Company updated = Company.builder()
                .id(company.getId()) // ⚠ 수정 시 ID 포함
                .name(dto.getName())
                .businessNumber(dto.getBusinessNumber())
                .representativeName(dto.getRepresentativeName())
                .address(dto.getAddress())
                .note(dto.getNote())
                .build();

        return CompanyDto.fromEntity(companyRepository.save(updated));
    }



    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보가 없습니다. ID: " + id));
        return CompanyDto.fromEntity(company);
    }

    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보가 없습니다. ID: " + companyId));
        company.markAsDeleted();
    }
}
