package kr.co.ksgk.ims.domain.settlement.dto.response;

import kr.co.ksgk.ims.domain.settlement.entity.StorageFreePeriodConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StorageFreePeriodConfigResponse(
        Long id,
        Long companyId,
        String companyName,
        Long productId,
        String productName,
        Integer freePeriodDays,
        LocalDate effectiveFrom,
        LocalDate effectiveUntil,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StorageFreePeriodConfigResponse from(StorageFreePeriodConfig config) {
        return new StorageFreePeriodConfigResponse(
                config.getId(),
                config.getCompany().getId(),
                config.getCompany().getName(),
                config.getProduct() != null ? config.getProduct().getId() : null,
                config.getProduct() != null ? config.getProduct().getName() : null,
                config.getFreePeriodDays(),
                config.getEffectiveFrom(),
                config.getEffectiveUntil(),
                config.getIsActive(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }
}
