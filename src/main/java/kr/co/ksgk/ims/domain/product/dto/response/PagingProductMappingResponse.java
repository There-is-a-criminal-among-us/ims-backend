package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.RawProduct;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingProductMappingResponse(
        PageResponse page,
        List<ProductMappingResponse> productMappings
) {
    public static PagingProductMappingResponse of(Page<RawProduct> pageRawProduct, List<ProductMappingResponse> productMappings) {
        return PagingProductMappingResponse.builder()
                .page(PageResponse.from(pageRawProduct))
                .productMappings(productMappings)
                .build();
    }
}
