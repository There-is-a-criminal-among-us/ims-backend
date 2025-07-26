package kr.co.ksgk.ims.domain.brand.service;

import kr.co.ksgk.ims.domain.brand.dto.request.BrandRequest;
import kr.co.ksgk.ims.domain.brand.dto.response.BrandResponse;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
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
public class BrandService {

    private final BrandRepository brandRepository;
    private final CompanyRepository companyRepository;

    //등록
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        Brand brand = request.toEntity(company);
        Brand saved = brandRepository.save(brand);
        return BrandResponse.from(saved);
    }

    //조회
    public BrandResponse getBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        return BrandResponse.from(brand);
    }

    //수정
    @Transactional
    public BrandResponse updateBrand(Long brandId, BrandRequest request) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        if (request.name() != null) {
            brand.updateName(request.name());
        }
        if (request.note() != null) {
            brand.updateNote(request.note());
        }
        return BrandResponse.from(brand);
    }

    //삭제
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        brand.softDelete();
    }
}
