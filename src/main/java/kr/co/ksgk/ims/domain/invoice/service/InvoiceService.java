package kr.co.ksgk.ims.domain.invoice.service;


import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.invoice.dto.InvoiceUploadRequestDto;
import kr.co.ksgk.ims.domain.invoice.dto.InvoiceUploadResponseDto;
import kr.co.ksgk.ims.domain.invoice.entity.Invoice;
import kr.co.ksgk.ims.domain.invoice.entity.InvoiceProduct;
import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService
{
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    public InvoiceUploadResponseDto uploadInvoice(InvoiceUploadRequestDto dto)
    {

        Company company = companyRepository.findById(dto.getCompanyId()).orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        Invoice invoice = Invoice.builder()
                .company(company)
                .name(dto.getName())
                .phone(dto.getPhone())
                .number(dto.getNumber())
                .invoiceUrl(dto.getInvoiceImageUrl())
                .productUrl(dto.getProductImageUrl())
                .build();

        List<InvoiceProduct> productEntities = dto.getProducts().stream().map
                (p ->
                    {
                        Product product = productRepository.findById(p.getProductId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

                        return new InvoiceProduct(invoice, product, p.getReturnedQuantity(), p.getResaleableQuantity(), p.getNote());
                    }
                ).toList();

        invoice.getInvoiceProducts().addAll(productEntities);

        Invoice saved=invoiceRepository.save(invoice);

        return new InvoiceUploadResponseDto(saved.getId(),saved.getNumber(),saved.getName(),saved.getPhone(),saved.getCreatedAt().toString());

    }
}
