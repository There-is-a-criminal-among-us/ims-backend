package kr.co.ksgk.ims.domain.product.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.product.dto.ProductDTO;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional; // Optional 임포트 추가

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository; // Brand 엔티티를 찾기 위해 필요

    // 모든 제품 조회 (명세서에는 없지만 편의상 유지)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByDeletedAtIsNull();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 제품 조회
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return convertToDto(product);
    }

    // 제품 생성
    @Transactional
    public ProductDTO createProduct(ProductDTO.CreateRequest dto) {
        // 명세서에 companyId가 있지만, Product 엔티티에 직접 companyId 필드가 없으므로,
        // 현재는 brandId만 사용하여 Brand를 찾습니다.
        // companyId 관련 로직이 필요하다면 여기에 추가되어야 합니다 (예: company 엔티티 조회 등).
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + dto.getBrandId()));

        Product product = Product.builder()
                .brand(brand)
                .name(dto.getName())
                .note(dto.getNote())
                .build();
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    // 제품 업데이트 (PATCH 메서드에 맞춰 부분 업데이트 가능하도록 수정)
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO.UpdateRequest dto) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // DTO 필드가 null이 아닌 경우에만 업데이트
        boolean changed = false;
        if (dto.getName() != null && !dto.getName().equals(product.getName())) {
            product.updateName(dto.getName()); // Product 엔티티에 updateName 메소드 추가 필요
            changed = true;
        }
        if (dto.getNote() != null && !dto.getNote().equals(product.getNote())) {
            product.updateNote(dto.getNote()); // Product 엔티티에 updateNote 메소드 추가 필요
            changed = true;
        }

        // 변경된 경우에만 저장 (JPA Dirty Checking)
        if (changed) {
            // productRepository.save(product); // JpaRepository는 변경 감지 시 save 호출 불필요
            // @Transactional 내부에서 변경 감지 후 자동으로 DB에 반영
        }
        return convertToDto(product);
    }

    // 제품 삭제
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        product.setDeletedAt(LocalDateTime.now());
        // productRepository.save(product); // @Transactional 내부에서 변경 감지 후 자동으로 DB에 반영
    }


    private ProductDTO convertToDto(Product product) {
        return ProductDTO.builder()
                .productId(product.getId())
                .name(product.getName())
                .note(product.getNote())
                .created_at(product.getCreatedAt())
                .updated_at(product.getUpdatedAt())
                .build();

    }
}