package kr.co.ksgk.ims.domain.stock.dto.request;

import java.time.LocalDate;
import java.util.List;

public record TransactionUpdateRequest(
        Integer quantity,
        String note,
        LocalDate scheduledDate,
        LocalDate workDate,
        List<TransactionWorkRequest> works
) {
}
