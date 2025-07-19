package kr.co.ksgk.ims.domain.company.controller;

import kr.co.ksgk.ims.domain.company.dto.CompanyDto;
import kr.co.ksgk.ims.domain.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto) {
        CompanyDto created = companyService.createCompany(companyDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompany(@PathVariable Long companyId) {
        CompanyDto company = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(company);
    }

    @PatchMapping("/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long companyId, @RequestBody CompanyDto companyDto) {
        CompanyDto updated = companyService.updateCompany(companyId, companyDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }
}