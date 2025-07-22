package kr.co.ksgk.ims.domain.brand.controller;

import kr.co.ksgk.ims.domain.brand.dto.BrandDto;
import kr.co.ksgk.ims.domain.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    //조회
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrand(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrand(id));
    }

    //등록
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody BrandDto dto) {
        return ResponseEntity.ok(brandService.createBrand(dto));
    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id, @RequestBody BrandDto dto) {
        return ResponseEntity.ok(brandService.updateBrand(id, dto));
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
