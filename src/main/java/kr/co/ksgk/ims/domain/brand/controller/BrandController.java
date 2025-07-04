package kr.co.ksgk.ims.domain.brand.controller;

import kr.co.ksgk.ims.domain.brand.service.BrandService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    ResponseEntity<SuccessResponse<?>> getAllBrands() {
        return SuccessResponse.ok(brandService.getAllBrands());
    }

    @GetMapping("/{brandId}")
    ResponseEntity<SuccessResponse<?>> getBrandById(@PathVariable int brandId) {
        return SuccessResponse.ok(brandService.getBrandById(brandId));
    }
}
