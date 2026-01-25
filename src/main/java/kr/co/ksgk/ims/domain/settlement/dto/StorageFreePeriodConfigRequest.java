package kr.co.ksgk.ims.domain.settlement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StorageFreePeriodConfigRequest(
        @NotNull(message = "업체 ID는 필수입니다.")
        Long companyId,

        Long productId,  // null이면 업체 기본 설정

        @NotNull(message = "무료 기간은 필수입니다.")
        @Min(value = 0, message = "무료 기간은 0 이상이어야 합니다.")
        Integer freePeriodDays,

        LocalDate effectiveFrom,

        LocalDate effectiveUntil
) {
    public static StorageFreePeriodConfigRequest createCompanyDefault(Long companyId, Integer freePeriodDays) {
        return new StorageFreePeriodConfigRequest(companyId, null, freePeriodDays, null, null);
    }

    public static StorageFreePeriodConfigRequest createForProduct(Long companyId, Long productId, Integer freePeriodDays) {
        return new StorageFreePeriodConfigRequest(companyId, productId, freePeriodDays, null, null);
    }
}
