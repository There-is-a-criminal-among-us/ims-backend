package kr.co.ksgk.ims.domain.returns.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReturnExcelUploadResponse(
        int totalFiles,
        int successFiles,
        int errorFiles,
        int totalRows,
        int successCount,
        List<FileErrorSummary> errorDetails
) {
    @Builder
    public record FileErrorSummary(
            String fileName,
            String errorMessage,
            List<String> errorRows
    ) {}

    public static ReturnExcelUploadResponse of(int totalFiles, int successFiles, int errorFiles,
                                               int totalRows, int successCount, List<FileErrorSummary> errorDetails) {
        return ReturnExcelUploadResponse.builder()
                .totalFiles(totalFiles)
                .successFiles(successFiles)
                .errorFiles(errorFiles)
                .totalRows(totalRows)
                .successCount(successCount)
                .errorDetails(errorDetails)
                .build();
    }
}
