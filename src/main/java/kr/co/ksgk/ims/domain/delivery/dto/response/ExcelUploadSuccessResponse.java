package kr.co.ksgk.ims.domain.delivery.dto.response;

import lombok.Builder;

@Builder
public record ExcelUploadSuccessResponse(
        int totalFiles,
        int totalProducts,
        String message
) {
    public static ExcelUploadSuccessResponse of(int totalFiles, int totalProducts) {
        return ExcelUploadSuccessResponse.builder()
                .totalFiles(totalFiles)
                .totalProducts(totalProducts)
                .message("엑셀 파일 업로드가 완료되었습니다")
                .build();
    }
}