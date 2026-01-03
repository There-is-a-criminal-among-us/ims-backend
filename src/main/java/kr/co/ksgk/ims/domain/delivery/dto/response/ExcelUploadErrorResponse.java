package kr.co.ksgk.ims.domain.delivery.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ExcelUploadErrorResponse(
        String message,
        List<FileErrorDetail> fileErrors
) {
    @Builder
    public record FileErrorDetail(
            String fileName,
            String errorType,
            String errorMessage,
            List<String> notFoundProducts
    ) {
    }
    
    public static ExcelUploadErrorResponse of(List<FileErrorDetail> fileErrors) {
        return ExcelUploadErrorResponse.builder()
                .message("엑셀 파일 처리 중 오류가 발생했습니다")
                .fileErrors(fileErrors)
                .build();
    }
    
    public static ExcelUploadErrorResponse ofProductNotFound(List<FileErrorDetail> fileErrors) {
        return ExcelUploadErrorResponse.builder()
                .message("일부 품목명이 등록되지 않았습니다")
                .fileErrors(fileErrors)
                .build();
    }
}