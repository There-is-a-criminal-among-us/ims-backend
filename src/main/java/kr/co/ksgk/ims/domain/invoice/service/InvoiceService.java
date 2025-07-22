package kr.co.ksgk.ims.domain.invoice.service;

import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.invoice.dto.request.UploadedInfo;
import kr.co.ksgk.ims.domain.invoice.dto.response.InvoiceInfo;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public InvoiceInfo uploadInvoice(UploadedInfo request) {

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

        return InvoiceInfo.from(saved);
    }
}
