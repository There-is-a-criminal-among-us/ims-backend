package kr.co.ksgk.ims.domain.company.controller;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.dto.request.CompanyRequest;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import kr.co.ksgk.ims.domain.company.dto.response.PagingCompanyResponse;
import kr.co.ksgk.ims.domain.company.service.CompanyService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController implements CompanyApi {

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllCompanies(
            @Parameter(description = "사업자 검색")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PagingCompanyResponse response = companyService.getAllCompanies(search, PageRequest.of(page, size));
        return SuccessResponse.ok(response);
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