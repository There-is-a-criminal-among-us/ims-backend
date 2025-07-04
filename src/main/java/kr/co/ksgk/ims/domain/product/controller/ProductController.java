package kr.co.ksgk.ims.domain.product.controller;

import kr.co.ksgk.ims.domain.product.service.ProductService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    ResponseEntity<SuccessResponse<?>> getAllProducts() {
        return SuccessResponse.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    ResponseEntity<SuccessResponse<?>> getProductById(@PathVariable int productId) {
        return SuccessResponse.ok(productService.getProductById(productId));
    }
}
