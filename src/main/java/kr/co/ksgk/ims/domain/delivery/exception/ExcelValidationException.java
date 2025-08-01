package kr.co.ksgk.ims.domain.delivery.exception;

import kr.co.ksgk.ims.domain.delivery.dto.response.ExcelUploadErrorResponse;

public class ExcelValidationException extends RuntimeException {
    private final ExcelUploadErrorResponse errorResponse;

    public ExcelValidationException(ExcelUploadErrorResponse errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }

    public ExcelUploadErrorResponse getErrorResponse() {
        return errorResponse;
    }
}