package kr.co.ksgk.ims.domain.brand.service;

import kr.co.ksgk.ims.domain.brand.dto.BrandResponse;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandResponse> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brands.stream()
                .map(BrandResponse::from)
                .collect(Collectors.toList());
    }

    public BrandResponse getBrandById(int brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
        return BrandResponse.from(brand);
    }
}
