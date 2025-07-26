package kr.co.ksgk.ims.domain.brand.controller;

import kr.co.ksgk.ims.domain.brand.dto.request.BrandRequest;
import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.brand.service.BrandService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    //등록
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createBrand(@RequestBody BrandRequest request) {
        BrandResponse brandResponse = brandService.createBrand(request);
        return SuccessResponse.created(brandResponse);
    }

    //조회
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<?>> getBrand(@PathVariable Long id) {
        BrandResponse brandResponse = brandService.getBrand(id);
        return SuccessResponse.ok(brandResponse);
    }


    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse<?>> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        BrandResponse brandResponse = brandService.updateBrand(id, request);
        return SuccessResponse.ok(brandResponse);
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<?>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return SuccessResponse.noContent();
    }
}
