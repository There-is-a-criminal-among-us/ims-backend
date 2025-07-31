package kr.co.ksgk.ims.domain.brand.dto.response;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingBrandResponse(
        PageResponse page,
        List<BrandResponse> brands
) {
    public static PagingBrandResponse of(Page<Brand> pageBrand, List<BrandResponse> brands) {
        return PagingBrandResponse.builder()
                .page(PageResponse.from(pageBrand))
                .brands(brands)
                .build();
    }
}
