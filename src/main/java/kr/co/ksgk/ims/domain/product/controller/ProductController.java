package kr.co.ksgk.ims.domain.product.controller;

import kr.co.ksgk.ims.domain.product.dto.ProductDTO;
import kr.co.ksgk.ims.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 품목 등록
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO.CreateRequest dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    // 품목 정보 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // 품목 수정
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("productId") Long id, @RequestBody ProductDTO.UpdateRequest dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    // 품목 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 모든 품목 조회
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}