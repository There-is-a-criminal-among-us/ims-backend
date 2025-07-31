package kr.co.ksgk.ims.domain.brand.controller;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.ksgk.ims.domain.brand.dto.request.BrandRequest;
import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.brand.dto.response.PagingBrandResponse;
import kr.co.ksgk.ims.domain.brand.service.BrandService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SuccessResponse<?>> getAllBrands(
            @Parameter(description = "브랜드 검색")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PagingBrandResponse response = brandService.getAllBrands(search, PageRequest.of(page, size));
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
