package kr.co.ksgk.ims.domain.brand.controller;

import kr.co.ksgk.ims.domain.brand.dto.request.BrandRequest;
import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.brand.service.BrandService;
import kr.co.ksgk.ims.domain.company.dto.response.CompanyResponse;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    //등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createBrand(@RequestBody BrandRequest request) {
        BrandResponse brandResponse = brandService.createBrand(request);
        return SuccessResponse.created(brandResponse);
    }

    //모든 브랜드 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllBrands() {
        List<BrandResponse> response = brandService.getAllBrands();
        return SuccessResponse.ok(response);
    }

    //조회
    @GetMapping("/{brandId}")
    public ResponseEntity<SuccessResponse<?>> getBrand(@PathVariable Long brandId) {
        BrandResponse brandResponse = brandService.getBrand(brandId);
        return SuccessResponse.ok(brandResponse);
    }

    //수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{brandId}")
    public ResponseEntity<SuccessResponse<?>> updateBrand(@PathVariable Long brandId, @RequestBody BrandRequest request) {
        BrandResponse brandResponse = brandService.updateBrand(brandId, request);
        return SuccessResponse.ok(brandResponse);
    }

    //삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{brandId}")
    public ResponseEntity<SuccessResponse<?>> deleteBrand(@PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
        return SuccessResponse.noContent();
    }
}
