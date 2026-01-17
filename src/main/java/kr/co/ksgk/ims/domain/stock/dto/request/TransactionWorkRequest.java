package kr.co.ksgk.ims.domain.stock.dto.request;

import jakarta.validation.constraints.NotNull;

public record TransactionWorkRequest(
        @NotNull
        Long settlementItemId,
        Long settlementUnitId,
        Integer quantity,
        Integer cost
) {
}