package kr.co.ksgk.ims.domain.returns.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;
import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;

public record CreateReturnPartyRequest(
        @NotBlank
        String name,
        @NotNull
        Long brandId
) {
    public ReturnMall toReturnMall(Brand brand) {
        return ReturnMall.builder()
                .name(name)
                .brand(brand)
                .build();
    }

    public ReturnHandler toReturnHandler(Brand brand) {
        return ReturnHandler.builder()
                .name(name)
                .brand(brand)
                .build();
    }
}
