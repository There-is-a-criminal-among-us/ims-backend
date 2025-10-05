package kr.co.ksgk.ims.domain.returns.dto.response;

import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;
import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;
import lombok.Builder;

@Builder
public record ReturnPartyResponse(
        long id,
        String name,
        Long brandId,
        String brandName
) {
    public static ReturnPartyResponse from(ReturnMall returnMall) {
        return ReturnPartyResponse.builder()
                .id(returnMall.getId())
                .name(returnMall.getName())
                .brandId(returnMall.getBrand().getId())
                .brandName(returnMall.getBrand().getName())
                .build();
    }

    public static ReturnPartyResponse from(ReturnHandler returnHandler) {
        return ReturnPartyResponse.builder()
                .id(returnHandler.getId())
                .name(returnHandler.getName())
                .brandId(returnHandler.getBrand().getId())
                .brandName(returnHandler.getBrand().getName())
                .build();
    }
}
