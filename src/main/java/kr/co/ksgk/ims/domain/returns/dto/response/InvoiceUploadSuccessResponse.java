package kr.co.ksgk.ims.domain.returns.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InvoiceUploadSuccessResponse(
        int totalFiles,
        int totalUpdated,
        List<String> notFoundInvoices,
        String message
) {
    public static InvoiceUploadSuccessResponse of(int totalFiles, int totalUpdated, List<String> notFoundInvoices) {
        return InvoiceUploadSuccessResponse.builder()
                .totalFiles(totalFiles)
                .totalUpdated(totalUpdated)
                .notFoundInvoices(notFoundInvoices)
                .message("반송장 업로드가 완료되었습니다")
                .build();
    }
}