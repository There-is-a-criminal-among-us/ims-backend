package kr.co.ksgk.ims.domain.settlement.dto;

import java.util.List;

public record DeliverySheetUploadResponse(
        int year,
        int month,
        int successCount,
        int failureCount,
        List<FailedRow> failedRows
) {
    public record FailedRow(
            int rowNumber,
            String productName,
            String reason
    ) {}

    public static DeliverySheetUploadResponse success(int year, int month, int successCount) {
        return new DeliverySheetUploadResponse(year, month, successCount, 0, List.of());
    }

    public static DeliverySheetUploadResponse failure(int year, int month, List<FailedRow> failedRows) {
        return new DeliverySheetUploadResponse(year, month, 0, failedRows.size(), failedRows);
    }
}
