package kr.co.ksgk.ims.domain.delivery.dto.response;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import kr.co.ksgk.ims.global.common.PageResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingDeliveryResponse(
        PageResponse page,
        List<DeliveryResponse> deliveries
) {
    public static PagingDeliveryResponse of (Page<Delivery> pageDelivery, List<DeliveryResponse> deliveries) {
        return PagingDeliveryResponse.builder()
                .page(PageResponse.from(pageDelivery))
                .deliveries(deliveries)
                .build();
    }
}
