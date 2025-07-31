package kr.co.ksgk.ims.domain.product.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.product.dto.request.ProductMappingRequest;
import kr.co.ksgk.ims.domain.product.dto.request.ProductRequest;
import kr.co.ksgk.ims.domain.product.dto.response.PagingProductMappingResponse;
import kr.co.ksgk.ims.domain.product.dto.response.PagingProductResponse;
import kr.co.ksgk.ims.domain.product.dto.response.ProductMappingResponse;
import kr.co.ksgk.ims.domain.product.dto.response.ProductResponse;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.domain.product.repository.ProductMappingRepository;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.product.repository.RawProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final RawProductRepository rawProductRepository;
    private final ProductMappingRepository productMappingRepository;

    //등록
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        Product product = request.toEntity(brand);
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    public PagingProductResponse getAllProducts(String search, Pageable pageable) {
        Page<Product> pageProduct;
        if (search == null || search.isBlank()) {
            pageProduct = productRepository.findAll(pageable);
        } else {
            pageProduct = productRepository.findByNameContaining(search, pageable);
        }
        List<ProductResponse> products = pageProduct.getContent().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return PagingProductResponse.of(pageProduct, products);
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
        if (request.name() != null) product.updateName(request.name());
        if (request.note() != null) product.updateNote(request.note());
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    @Transactional
    public ProductMappingResponse createProductMapping(ProductMappingRequest request) {
        RawProduct rawProduct = request.toEntity();
        RawProduct savedRawProduct = rawProductRepository.save(rawProduct);
        List<ProductMapping> productMappings = new ArrayList<>();
        request.products().forEach(
                productMappingDetail -> {
                    Product product = productRepository.findById(productMappingDetail.productId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
                    productMappings.add(productMappingDetail.toEntity(product, savedRawProduct));
                });
        List<ProductMapping> savedProductMappings = productMappingRepository.saveAll(productMappings);
        return ProductMappingResponse.from(savedProductMappings);
    }

    public PagingProductMappingResponse getProductMapping(String search, Pageable pageable) {
        Page<RawProduct> pageRawProduct;
        if (search == null || search.isBlank()) {
            pageRawProduct = rawProductRepository.findAll(pageable);
        } else {
            pageRawProduct = rawProductRepository.findByNameContaining(search, pageable);
        }
        List<ProductMappingResponse> productMappings = pageRawProduct.getContent().stream()
                .map(rawProduct -> {
                    List<ProductMapping> mappings = productMappingRepository.findByRawProduct(rawProduct);
                    return ProductMappingResponse.from(mappings);
                })
                .collect(Collectors.toList());
        return PagingProductMappingResponse.of(pageRawProduct, productMappings);
    }

    @Transactional
    public void deleteProductMapping(Long rawProductId) {
        RawProduct rawProduct = rawProductRepository.findById(rawProductId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RAW_PRODUCT_NOT_FOUND));
        rawProductRepository.delete(rawProduct);
    }
}