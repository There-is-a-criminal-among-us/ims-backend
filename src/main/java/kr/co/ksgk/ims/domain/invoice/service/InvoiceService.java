package kr.co.ksgk.ims.domain.invoice.service;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.company.entity.Company;
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
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final InvoiceProductRepository invoiceProductRepository;
    private final S3Service s3Service;

    @Transactional
    public SimpleInvoiceInfoResponse uploadInvoice(UploadInvoiceInfoRequest request) {
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        String invoiceImageUrl = Optional.ofNullable(request.invoiceKeyName())
                .map(s3Service::generateStaticUrl)
                .orElse(null);
        String productImageUrl = Optional.ofNullable(request.productKeyName())
                .map(s3Service::generateStaticUrl)
                .orElse(null);
        Invoice invoice = request.toEntity(company, invoiceImageUrl, productImageUrl);
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
        return SimpleInvoiceInfoResponse.from(saved);
    }

    public PagingInvoiceInfoResponse getInvoiceList(String search, Pageable pageable) {
        Page<InvoiceProduct> invoiceProductPage;
        if (search == null || search.isBlank()) {
            invoiceProductPage = invoiceProductRepository.findAll(pageable);
        } else {
            invoiceProductPage = invoiceProductRepository.findInvoiceProductByNameOrNumber(search, pageable);
        }
        List<SimpleInvoiceProductInfoResponse> simpleInvoiceProductInfoResponses = invoiceProductPage.getContent().stream()
                .map(SimpleInvoiceProductInfoResponse::from)
                .toList();
        return PagingInvoiceInfoResponse.of(invoiceProductPage, simpleInvoiceProductInfoResponses);
    }

    public InvoiceInfoResponse getInvoiceInfo(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        return InvoiceInfoResponse.from(invoice);
    }

    @Transactional
    public InvoiceInfoResponse updateInvoiceInfo(Long invoiceId, InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        if (request.name() != null) invoice.updateName(request.name());
        if (request.phone() != null) invoice.updatePhone(request.phone());
        if (request.invoiceUrl() != null) invoice.updateInvoiceUrl(request.invoiceUrl());
        List<InvoiceProduct> invoiceProducts = request.products().stream()
                .map(p -> {
                    Product product = productRepository.findById(p.productId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
                    return new InvoiceProduct(invoice, product, p.returnedQuantity(), p.resalableQuantity(), p.note());
                })
                .toList();
        invoice.updateInvoiceProduct(invoiceProducts);
        return InvoiceInfoResponse.from(invoice);
    }

    @Transactional
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVOICE_NOT_FOUND));
        invoiceRepository.delete(invoice);
    }
}