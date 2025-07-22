package kr.co.ksgk.ims.domain.brand.service;

import kr.co.ksgk.ims.domain.brand.dto.BrandDto;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getDeletedAt() == null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BrandDto getBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        return toDTO(brand);
    }

    public BrandDto createBrand(BrandDto dto) {
        Brand brand = Brand.builder()
                .name(dto.getName())
                .note(dto.getNote())
                .build();
        brandRepository.save(brand);
        return toDTO(brand);
    }

    public BrandDto updateBrand(Long id, BrandDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        brand.update(dto.getName(), dto.getNote());
        return toDTO(brand);
    }

    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        brand.softDelete();
    }

    private BrandDto toDTO(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .note(brand.getNote())
                .deletedAt(brand.getDeletedAt())
                .build();
    }
}
