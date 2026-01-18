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
import kr.co.ksgk.ims.domain.settlement.entity.CalculationType;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementUnit;
import kr.co.ksgk.ims.domain.settlement.repository.SettlementUnitRepository;
import kr.co.ksgk.ims.domain.stock.entity.DailyStock;
import kr.co.ksgk.ims.domain.stock.repository.StockRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
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
    private final SettlementUnitRepository settlementUnitRepository;

    //등록
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        Product product = request.toEntity(brand);

        if (request.sizeUnitId() != null) {
            SettlementUnit sizeUnit = getValidatedSizeUnit(request.sizeUnitId());
            product.updateSizeUnit(sizeUnit);
        }

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
    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        ProductResponse productResponse =ProductResponse.from(product);
        ProductStatusResponse productStatusResponse = getProductStatus(product,YearMonth.now());
        return ProductDetailResponse.builder()
                .product(productResponse)
                .productStatus(productStatusResponse)
                .build();
    }

    //수정
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        if (request.name() != null) product.updateName(request.name());
        if (request.note() != null) product.updateNote(request.note());
        if (request.storageType() != null) {
            product.updateStorageSettings(
                    request.storageType(),
                    request.cbm(),
                    request.storagePricePerCbm(),
                    request.quantityPerPallet(),
                    request.storagePricePerPallet()
            );
        }
        if (request.sizeUnitId() != null) {
            SettlementUnit sizeUnit = getValidatedSizeUnit(request.sizeUnitId());
            product.updateSizeUnit(sizeUnit);
        }
        return ProductResponse.from(product);
    }

    private SettlementUnit getValidatedSizeUnit(Long sizeUnitId) {
        SettlementUnit unit = settlementUnitRepository.findById(sizeUnitId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SETTLEMENT_UNIT_NOT_FOUND));

        if (unit.getItem().getCalculationType() != CalculationType.SIZE) {
            throw new BusinessException(ErrorCode.INVALID_SIZE_UNIT);
        }

        return unit;
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

    private ProductStatusResponse getProductStatus(Product product, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Product> products = List.of(product);
        List<DailyStock> monthlyDailyStocks = stockRepository.findAllByProductsAndDateBetween(
                products, startDate, endDate);

        List<InvoiceProduct> monthlyInvoiceProducts = invoiceProductRepository.findByProductAndInvoiceCreatedAtBetween(
            product, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1).minusNanos(1));

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

        int currentStock = monthlyDailyStocks.isEmpty() ? 0 : monthlyDailyStocks.get(monthlyDailyStocks.size() - 1).getCurrentStock();

        return ProductStatusResponse.builder()
            .productId(product.getId())
            .currentStock(currentStock)
            .totalIncoming(totalIncoming)
            .totalOutgoing(totalOutgoing)
            .returnedQuantity(returnedQuantity)
            .resalableQuantity(resalableQuantity)
            .totalAdjustment(adjustmentTotal)
            .build();
}
}