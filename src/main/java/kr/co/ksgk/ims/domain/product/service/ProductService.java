package kr.co.ksgk.ims.domain.product.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceProductRepository;
import kr.co.ksgk.ims.domain.product.dto.request.ProductMappingRequest;
import kr.co.ksgk.ims.domain.product.dto.request.ProductCreateRequest;
import kr.co.ksgk.ims.domain.product.dto.request.ProductUpdateRequest;
import kr.co.ksgk.ims.domain.product.dto.response.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.domain.product.repository.ProductMappingRepository;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.product.repository.RawProductRepository;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
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
    private final StockRepository stockRepository;
    private final InvoiceProductRepository invoiceProductRepository;

    //등록
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
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
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
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

public ProductStatusResponse getProductStatus(Long productId, YearMonth yearMonth) {
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    // 1. productId로 Product 엔티티를 조회
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

    // 2. Product 엔티티를 리스트로 만들어 stockRepository 메서드에 전달
    List<Product> products = List.of(product);
    List<DailyStock> monthlyDailyStocks = stockRepository.findAllByProductsAndDateBetween(
            products, startDate, endDate);

    // 3. 해당 월의 InvoiceProduct 엔티티 목록 조회 (LocalDateTime으로 변환)
    List<InvoiceProduct> monthlyInvoiceProducts = invoiceProductRepository.findByProductAndInvoiceCreatedAtBetween(
            product, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1).minusNanos(1));

    // 4. 합산 로직
    int totalIncoming = monthlyDailyStocks.stream()
            .mapToInt(DailyStock::getInboundTotal)
            .sum();

    int totalOutgoing = monthlyDailyStocks.stream()
            .mapToInt(DailyStock::getOutboundTotal)
            .sum();

    int adjustmentTotal = monthlyDailyStocks.stream()
            .mapToInt(DailyStock::getAdjustmentTotal)
            .sum();

    int returnedQuantity = monthlyInvoiceProducts.stream()
            .mapToInt(InvoiceProduct::getReturnedQuantity)
            .sum();

    int resalableQuantity = monthlyInvoiceProducts.stream()
            .mapToInt(InvoiceProduct::getResalableQuantity)
            .sum();

    // 5. 현재고 수량은 해당 월의 마지막 날짜 기준으로 가져옵니다.
    int currentStock = monthlyDailyStocks.isEmpty() ? 0 : monthlyDailyStocks.get(monthlyDailyStocks.size() - 1).getCurrentStock();

    // 6. 최종 DTO 빌드 및 반환
    return ProductStatusResponse.builder()
            .productId(productId)
            .currentStock(currentStock)
            .totalIncoming(totalIncoming)
            .totalOutgoing(totalOutgoing)
            .returnedQuantity(returnedQuantity)
            .resalableQuantity(resalableQuantity)
            .adjustmentTotal(adjustmentTotal)
            .build();
    }
}