package kr.co.ksgk.ims.domain.company.controller;

import kr.co.ksgk.ims.domain.company.dto.TreeResponse;
import kr.co.ksgk.ims.domain.company.service.CompanyService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/tree")
    public ResponseEntity<SuccessResponse<?>> getCompanyTree() {
        TreeResponse treeResponse = companyService.getCompanyTree();
        return SuccessResponse.ok(treeResponse);
    }
}
