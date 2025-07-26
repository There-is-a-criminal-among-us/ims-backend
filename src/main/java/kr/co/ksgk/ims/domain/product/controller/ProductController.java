package kr.co.ksgk.ims.domain.product.controller;

import kr.co.ksgk.ims.domain.product.dto.request.ProductRequest;
import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.domain.product.service.ProductService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    //등록
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createProduct(@RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return SuccessResponse.created(response);
    }

    //조회
    @GetMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return SuccessResponse.ok(response);
    }

    //수정
    @PatchMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) { // ProductRequest를 직접 사용
        ProductResponse response = productService.updateProduct(productId, request);
        return SuccessResponse.ok(response);
    }

    //삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return SuccessResponse.noContent();
    }
}