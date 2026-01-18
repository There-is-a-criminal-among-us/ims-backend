package kr.co.ksgk.ims.domain.settlement.dto;

import kr.co.ksgk.ims.domain.settlement.entity.Settlement;
import kr.co.ksgk.ims.domain.settlement.entity.SettlementStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SettlementResponse(
        Long id,
        Integer year,
        Integer month,
        Long brandId,
        String brandName,
        Long companyId,
        String companyName,
        SettlementStatus status,
        LocalDateTime confirmedAt,
        String confirmedByName,
        List<SettlementDetailResponse> details,
        Integer totalAmount
) {
    public static SettlementResponse from(Settlement settlement, List<SettlementDetailResponse> details) {
        int totalAmount = details.stream()
                .mapToInt(d -> d.amount() != null ? d.amount() : 0)
                .sum();

        return new SettlementResponse(
                settlement.getId(),
                settlement.getYear(),
                settlement.getMonth(),
                settlement.getBrand().getId(),
                settlement.getBrand().getName(),
                settlement.getBrand().getCompany().getId(),
                settlement.getBrand().getCompany().getName(),
                settlement.getStatus(),
                settlement.getConfirmedAt(),
                settlement.getConfirmedBy() != null ? settlement.getConfirmedBy().getName() : null,
                details,
                totalAmount
        );
    }
}
