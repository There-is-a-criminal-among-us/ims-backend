package kr.co.ksgk.ims.domain.product.dto;

import kr.co.ksgk.ims.domain.brand.dto.BrandDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

// 제품 응답을 위한 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String name;
    private String note;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;


    // 제품 생성 요청을 위한 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private Long companyId;
        private Long brandId;
        private String name;
        private String note;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String name;
        private String note;
    }
}