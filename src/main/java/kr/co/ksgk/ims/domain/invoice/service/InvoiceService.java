package kr.co.ksgk.ims.domain.invoice.service;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.invoice.dto.request.InvoiceUpdateRequest;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadedInfo;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceProductInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.PagingInvoiceInfoResponse;
import kr.co.ksgk.ims.domain.invoice.dto.response.SimpleInvoiceInfo;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceProductRepository;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final InvoiceProductRepository invoiceProductRepository;

    @Transactional
    public SimpleInvoiceInfo uploadInvoice(UploadedInfo request) {

        Company company = companyRepository.findById(request.companyId()).orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        Invoice invoice = request.toEntity(company);
        List<InvoiceProduct> productEntities = request.products().stream()
                .map(p -> {
                    Product product = productRepository.findById(p.productId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

                    return new InvoiceProduct(
                            invoice,
                            product,
                            p.returnedQuantity(),
                            p.resaleableQuantity(),
                            p.note()
                    );
                })
                .toList();

        invoice.getInvoiceProducts().addAll(productEntities);
        Invoice saved = invoiceRepository.save(invoice);

        return SimpleInvoiceInfo.from(saved);
    }

    public PagingInvoiceInfoResponse getInvoiceList(String search, Pageable pageable) {
        Page<InvoiceProduct> invoiceProductPage;
        if (search == null || search.isBlank()) {
            invoiceProductPage=invoiceProductRepository.findAll(pageable);
        } else {
            invoiceProductPage=invoiceProductRepository.findInvoiceProductByNameOrNumber(search,pageable);
        }

        List<SimpleInvoiceProductInfoResponse> simpleInvoiceProductInfoRespons =invoiceProductPage.getContent().stream()
                .map(SimpleInvoiceProductInfoResponse::from)
                .toList();

        return PagingInvoiceInfoResponse.of(invoiceProductPage, simpleInvoiceProductInfoRespons);
    }

    public InvoiceInfoResponse getInvoiceInfo(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        return InvoiceInfoResponse.from(invoice);
    }

    @Transactional
    public InvoiceInfoResponse updateInvoiceInfo(Long invoiceId, InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        List<InvoiceProduct> invoiceProducts = request.products().stream()
                .map(p -> {
                    Product product = productRepository.findById(p.productId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
                    return new InvoiceProduct(invoice, product, p.returnedQuantity(), p.resaleableQuantity(), p.note());
                })
                .toList();

        if(request.name()!=null)invoice.updateName(request.name());
        if(request.phone()!=null)invoice.updatePhone(request.phone());
        if(request.invoiceUrl()!=null)invoice.updateInvoiceUrl(request.invoiceUrl());
        invoice.updateInvoiceProduct(invoiceProducts);

        return InvoiceInfoResponse.from(invoice);
    }

    @Transactional
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice=invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        invoiceRepository.delete(invoice);
    }
}
