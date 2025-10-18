package kr.co.ksgk.ims.domain.returns.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.ksgk.ims.domain.returns.entity.OrderType;
import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;

public record CreateReturnRequest(
        @NotBlank String buyer,
        @NotBlank String receiver,
        @NotBlank String address,
        @NotBlank String phone,
        @NotBlank String productName,
        @NotNull Integer quantity,
        @NotBlank String originalInvoice,
        String note,
        @NotNull Long handlerId,
        @NotNull Long mallId,
        @NotNull OrderType orderType
) {
    public ReturnInfo toEntity(ReturnHandler returnHandler, ReturnMall returnMall) {
        return ReturnInfo.builder()
                .buyer(buyer)
                .receiver(receiver)
                .address(address)
                .phone(phone)
                .productName(productName)
                .quantity(quantity)
                .originalInvoice(originalInvoice)
                .note(note)
                .returnHandler(returnHandler)
                .returnMall(returnMall)
                .orderType(orderType)
                .build();
    }
}
