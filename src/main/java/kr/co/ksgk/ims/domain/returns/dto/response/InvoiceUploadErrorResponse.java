package kr.co.ksgk.ims.domain.returns.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InvoiceUploadErrorResponse(
        String message,
        List<FileErrorDetail> fileErrors
) {
    @Builder
    public record FileErrorDetail(
            String fileName,
            String errorType,
            String errorMessage,
            List<String> notFoundInvoices
    ) {
    }

    public static InvoiceUploadErrorResponse of(List<FileErrorDetail> fileErrors) {
        return InvoiceUploadErrorResponse.builder()
                .message("엑셀 파일 처리 중 오류가 발생했습니다")
                .fileErrors(fileErrors)
                .build();
    }
}