package kr.co.ksgk.ims.domain.returns.exception;

import kr.co.ksgk.ims.domain.returns.dto.response.InvoiceUploadErrorResponse;

public class InvoiceValidationException extends RuntimeException {
    private final InvoiceUploadErrorResponse errorResponse;

    public InvoiceValidationException(InvoiceUploadErrorResponse errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }

    public InvoiceUploadErrorResponse getErrorResponse() {
        return errorResponse;
    }
}