package kr.co.ksgk.ims.domain.product.dto.response;

import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingProductResponse(
        PageResponse page,
        List<ProductResponse> products
) {
    public static PagingProductResponse of(Page<Product> pageProduct, List<ProductResponse> products) {
        return PagingProductResponse.builder()
                .page(PageResponse.from(pageProduct))
                .products(products)
                .build();
    }
}
