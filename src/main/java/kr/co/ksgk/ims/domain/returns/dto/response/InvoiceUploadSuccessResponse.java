package kr.co.ksgk.ims.domain.returns.dto.response;

import lombok.Builder;

@Builder
public record InvoiceUploadSuccessResponse(
        int totalFiles,
        int totalUpdated,
        String message
) {
    public static InvoiceUploadSuccessResponse of(int totalFiles, int totalUpdated) {
        return InvoiceUploadSuccessResponse.builder()
                .totalFiles(totalFiles)
                .totalUpdated(totalUpdated)
                .message("반송장 업로드가 완료되었습니다")
                .build();
    }
}