package kr.co.ksgk.ims.domain.returns.dto.response;

/** Public diagnostics only. SQL, exception messages and stack traces stay in server logs. */
public record InvoiceUploadFailureResponse(
        int status,
        String code,
        String message,
        String errorId,
        String errorType,
        String stage,
        String fileName,
        Integer rowNumber
) {
}
