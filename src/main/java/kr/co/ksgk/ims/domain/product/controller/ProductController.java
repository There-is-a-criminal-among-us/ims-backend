package kr.co.ksgk.ims.domain.product.controller;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.ksgk.ims.domain.product.dto.request.ProductRequest;
import kr.co.ksgk.ims.domain.product.dto.response.PagingProductResponse;
import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.domain.product.service.ProductService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    //등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createProduct(@RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return SuccessResponse.created(response);
    }

    //모든 품목 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllProducts(
            @Parameter(description = "품목 검색")
            @RequestParam(defaultValue = "") String search,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingProductResponse response = productService.getAllProducts(search, PageRequest.of(page, size));
        return SuccessResponse.ok(response);
    }
    //조회
    @GetMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return SuccessResponse.ok(response);
    }

    //수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) { // ProductRequest를 직접 사용
        ProductResponse response = productService.updateProduct(productId, request);
        return SuccessResponse.ok(response);
    }

    //삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<SuccessResponse<?>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return SuccessResponse.noContent();
    }
}