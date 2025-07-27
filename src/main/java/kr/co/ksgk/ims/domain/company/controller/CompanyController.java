package kr.co.ksgk.ims.domain.company.controller;

import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.dto.request.CompanyRequest;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import kr.co.ksgk.ims.domain.company.service.CompanyService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tree")
    public ResponseEntity<SuccessResponse<?>> getCompanyTree() {
        TreeResponse treeResponse = companyService.getCompanyTree();
        return SuccessResponse.ok(treeResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createCompany(@RequestBody CompanyRequest request) {
        CompanyResponse companyResponse = companyService.createCompany(request);
        return SuccessResponse.created(companyResponse);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<SuccessResponse<?>> getCompany(@PathVariable Long companyId) {
        CompanyResponse companyResponse = companyService.getCompany(companyId);
        return SuccessResponse.ok(companyResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{companyId}")
    public ResponseEntity<SuccessResponse<?>> updateCompany(@PathVariable Long companyId, @RequestBody CompanyRequest request) {
        CompanyResponse updated = companyService.updateCompany(companyId, request);
        return SuccessResponse.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{companyId}")
    public ResponseEntity<SuccessResponse<?>> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return SuccessResponse.noContent();
    }
}