package kr.co.ksgk.ims.domain.company.controller;

import kr.co.ksgk.ims.domain.company.service.CompanyService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    ResponseEntity<SuccessResponse<?>> getAllCompanies() {
        return SuccessResponse.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{companyId}")
    ResponseEntity<SuccessResponse<?>> getCompanyById(@PathVariable int companyId) {
        return SuccessResponse.ok(companyService.getCompanyById(companyId));
    }
}
