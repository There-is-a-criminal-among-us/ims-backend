package kr.co.ksgk.ims.global.error;

import kr.co.ksgk.ims.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Valid & Validated annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(">>> handle: MethodArgumentNotValidException ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * ModelAttribute annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error(">>> handle: BindException ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * RequestParam annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(">>> handle: MethodArgumentTypeMismatchException ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * 지원하지 않는 HTTP method로 요청 시 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(">>> handle: HttpRequestMethodNotSupportedException ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorBaseResponse);
    }

    /**
     * 지원하지 않는 리소스 요청 시 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(">>> handle: NoResourceFoundException ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBaseResponse);
    }

    /**
     * 잘못된 Enum 값에 대한 error를 handling합니다. (HttpMessageNotReadableException)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if (e.getMessage().contains("Enum")) {
            log.error(">>> handle: HttpMessageNotReadableException (Invalid Enum value)", e);
            final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
        }
        log.error(">>> handle: HttpMessageNotReadableException (General parse error)", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBaseResponse);
    }

    /**
     * 파일 업로드 시 파일 크기 초과로 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(">>> handle: MaxUploadSizeExceededException (파일 크기 초과)", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FILE_SIZE_EXCEEDED);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    /**
     * 권한이 없는 사용자가 접근할 때 발생하는 error를 handling합니다.
     */
//    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
//    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
//        log.warn(">>> handle: AccessDeniedException or AuthorizationDeniedException", e);
//        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN);
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
//    }

    /**
     * BusinessException을 handling합니다.
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        log.error(">>> handle: BusinessException ", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorBaseResponse = ErrorResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorBaseResponse);
    }

    /**
     * 위에서 정의한 Exception을 제외한 모든 예외를 handling합니다.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(">>> handle: Exception ", e);
        final ErrorResponse errorBaseResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBaseResponse);
    }
}
