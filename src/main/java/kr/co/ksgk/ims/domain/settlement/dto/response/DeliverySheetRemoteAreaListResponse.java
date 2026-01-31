package kr.co.ksgk.ims.domain.settlement.dto.response;

import java.util.List;

public record DeliverySheetRemoteAreaListResponse(
        Integer totalFee,
        List<DeliverySheetRemoteAreaResponse> items
) {
    public static DeliverySheetRemoteAreaListResponse of(List<DeliverySheetRemoteAreaResponse> items) {
        int total = items.stream()
                .mapToInt(item -> item.totalFee() != null ? item.totalFee() : 0)
                .sum();
        return new DeliverySheetRemoteAreaListResponse(total, items);
    }
}
