package kr.co.ksgk.ims.domain.settlement.dto.request;

public record SettlementDetailUpdateRequest(
        Integer quantity,
        Integer unitPrice,
        Long amount,
        String note
) {}
