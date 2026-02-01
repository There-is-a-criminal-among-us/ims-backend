package kr.co.ksgk.ims.domain.settlement.dto.response;

import java.util.List;

public record DeliverySheetReturnListResponse(
        Integer totalAmount,
        List<DeliverySheetReturnResponse> items
) {
    public static DeliverySheetReturnListResponse of(List<DeliverySheetReturnResponse> items) {
        int total = items.stream()
                .mapToInt(item -> item.amount() != null ? item.amount() : 0)
                .sum();
        return new DeliverySheetReturnListResponse(total, items);
    }
}
