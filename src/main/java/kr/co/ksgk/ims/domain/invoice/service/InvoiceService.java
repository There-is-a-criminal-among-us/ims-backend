package kr.co.ksgk.ims.domain.invoice.service;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.invoice.dto.request.InvoiceUpdateRequest;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadInvoiceInfoRequest;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceProductInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.PagingInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceProductRepository;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.domain.stock.service.StockCacheInvalidator;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final InvoiceProductRepository invoiceProductRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final StockCacheInvalidator cacheInvalidator;

    @Transactional
    public SimpleInvoiceInfoResponse createInvoice(UploadInvoiceInfoRequest request) {
        Invoice invoice = request.toEntity();
        List<InvoiceProduct> productEntities = request.products().stream()
                .map(p -> {
                    Product product = productRepository.findById(p.productId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
                    return new InvoiceProduct(
                            invoice,
                            product,
                            p.returnedQuantity(),
                            p.resalableQuantity(),
                            p.note()
                    );
                })
                .toList();
        invoice.getInvoiceProducts().addAll(productEntities);
        Invoice saved = invoiceRepository.save(invoice);
        
        // 캐시 무효화: 새로운 Invoice 생성 시 - 관련된 모든 Product 캐시 무효화
        invalidateInvoiceCaches(productEntities);
        
        return SimpleInvoiceInfoResponse.from(saved);
    }

    public PagingInvoiceInfoResponse getInvoiceList(Long memberId, String search, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Page<InvoiceProduct> invoiceProductPage;
        if (member.getRole().equals(Role.ADMIN) || member.getRole().equals(Role.OCR)) {
            // ADMIN and OCR can see all invoice products
            if (search == null || search.isBlank()) {
                invoiceProductPage = invoiceProductRepository.findAll(pageable);
            } else {
                invoiceProductPage = invoiceProductRepository.findInvoiceProductByNameOrNumber(search, pageable);
            }
        } else {
            // MEMBER can only see invoice products from their managed brands/companies
            Set<Long> allowedProductIds = getAllowedProductIds(member);
            
            if (search == null || search.isBlank()) {
                invoiceProductPage = invoiceProductRepository.findByProductIdIn(allowedProductIds, pageable);
            } else {
                invoiceProductPage = invoiceProductRepository.findInvoiceProductByNameOrNumberOrInvoiceNumberAndProductIdIn(search, allowedProductIds, pageable);
            }
        }
        
        List<SimpleInvoiceProductInfoResponse> simpleInvoiceProductInfoResponses = invoiceProductPage.getContent().stream()
                .map(SimpleInvoiceProductInfoResponse::from)
                .toList();
        return PagingInvoiceInfoResponse.of(invoiceProductPage, simpleInvoiceProductInfoResponses);
    }
    
    private Set<Long> getAllowedProductIds(Member member) {
        Set<Long> memberBrandIds = member.getMemberBrands().stream()
                .map(mb -> mb.getBrand().getId())
                .collect(Collectors.toSet());
        
        if (!memberBrandIds.isEmpty()) {
            // If member has brand permissions, get products from those brands
            return productRepository.findIdsByBrandIdIn(memberBrandIds);
        } else {
            // If member has company permissions, get products from those companies
            Set<Long> memberCompanyIds = member.getMemberCompanies().stream()
                    .map(mc -> mc.getCompany().getId())
                    .collect(Collectors.toSet());
            return productRepository.findIdsByCompanyIdIn(memberCompanyIds);
        }
    }

    public InvoiceInfoResponse getInvoiceInfo(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        return InvoiceInfoResponse.from(invoice, s3Service);
    }

    @Transactional
    public InvoiceInfoResponse updateInvoiceInfo(Long invoiceId, InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        if (request.name() != null) invoice.updateName(request.name());
        if (request.phone() != null) invoice.updatePhone(request.phone());
        if (request.invoiceKeyName() != null) invoice.updateInvoiceKeyName(request.invoiceKeyName());
        if (request.productKeyName() != null) invoice.updateProductKeyName(request.productKeyName());
        List<InvoiceProduct> invoiceProducts = request.products().stream()
                .map(p -> {
                    Product product = productRepository.findById(p.productId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
                    return new InvoiceProduct(invoice, product, p.returnedQuantity(), p.resalableQuantity(), p.note());
                })
                .toList();
        invoice.updateInvoiceProduct(invoiceProducts);
        
        // 캐시 무효화: Invoice 업데이트 시 - 관련된 모든 Product 캐시 무효화
        invalidateInvoiceCaches(invoiceProducts);
        
        return InvoiceInfoResponse.from(invoice, s3Service);
    }

    @Transactional
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        
        // 캐시 무효화: Invoice 삭제 시 - 관련된 모든 Product 캐시 무효화
        invalidateInvoiceCaches(invoice.getInvoiceProducts());
        
        invoiceRepository.delete(invoice);
    }
    
    private void invalidateInvoiceCaches(List<InvoiceProduct> invoiceProducts) {
        invoiceProducts.forEach(invoiceProduct -> {
            Long productId = invoiceProduct.getProduct().getId();
            cacheInvalidator.invalidateCacheForProductIfToday(productId);
        });
    }
}