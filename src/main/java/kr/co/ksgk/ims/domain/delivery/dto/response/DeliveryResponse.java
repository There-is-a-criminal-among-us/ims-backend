package kr.co.ksgk.ims.domain.delivery.dto.response;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DeliveryResponse(
        long id,
        String companyName,
        String brandName,
        String rawProductName,
        int quantity,
        LocalDateTime createdAt
) {
    public static DeliveryResponse from(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .companyName(delivery.getRawProduct().getProductMappings().get(0).getProduct().getBrand().getCompany().getName())
                .brandName(delivery.getRawProduct().getProductMappings().get(0).getProduct().getBrand().getName())
                .rawProductName(delivery.getRawProduct().getName())
                .quantity(delivery.getQuantity())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
}
