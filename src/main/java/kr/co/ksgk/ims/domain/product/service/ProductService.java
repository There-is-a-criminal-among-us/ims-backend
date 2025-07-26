package kr.co.ksgk.ims.domain.product.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.product.dto.request.ProductRequest;
import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    //등록
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

        Product product = request.toEntity(brand);
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    //조회
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    //수정
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.name() != null) {
            product.updateName(request.name());
        }
        if (request.note() != null) {
            product.updateNote(request.note());
        }
        return ProductResponse.from(product);
    }
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        product.softDelete();
    }
}