package kr.co.ksgk.ims.domain.brand.dto.response;

import jdk.jshell.Snippet;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import lombok.Builder;


@Builder
public record BrandResponse(
        long id,
        String name,
        String note
) {
    public static BrandResponse from(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .note(brand.getNote())
                .build();
    }
}