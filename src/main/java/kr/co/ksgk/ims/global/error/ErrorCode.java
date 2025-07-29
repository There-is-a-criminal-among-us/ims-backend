package kr.co.ksgk.ims.global.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "ENUM 입력값이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C003", "리소스 접근 권한이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "C004", "액세스 토큰의 형식이 올바르지 않습니다. Bearer 타입을 확인해 주세요."),
    INVALID_ACCESS_TOKEN_VALUE(HttpStatus.UNAUTHORIZED, "C005", "액세스 토큰의 값이 올바르지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "C006", "액세스 토큰이 만료되었습니다. 재발급 받아주세요."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "C007", "리프레시 토큰의 형식이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN_VALUE(HttpStatus.UNAUTHORIZED, "C008", "리프레시 토큰의 값이 올바르지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "C009", "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "C010", "일치하지 않는 리프레시 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C011", "리소스 접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C012", "해당 리소스를 찾을 수 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C013", "엔티티를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C014", "잘못된 HTTP method 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, "C015", "이미 존재하는 리소스입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "C016", "이미지 최대 크기를 초과하였습니다."),
    INVALID_IMAGE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "C017", "지원하지 않는 이미지 형식입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C018", "서버 내부 오류입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C019", "파일 업로드에 실패하였습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "C020", "리프레시 토큰이 존재하지 않습니다."),

    // Auth
    JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A001", "JSON 파싱에 실패하였습니다."),
    NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "저장된 리프레시 토큰이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A003", "아이디나 비밀번호가 잘못되었습니다."),
    INVALID_AUTHORITY(HttpStatus.FORBIDDEN, "A004", "권한이 존재하지 않습니다."),

    // Member
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "M001", "이미 존재하는 사용자 이름입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "사용자가 존재하지 않습니다."),
    MEMBER_MANAGE_CONFLICT(HttpStatus.BAD_REQUEST, "M003", "사용자는 회사와 브랜드 중 하나만 관리할 수 있습니다."),

    // Brand
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "브랜드를 찾을 수 없습니다."),

    // Company
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "업체를 찾을 수 없습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "제품을 찾을 수 없습니다."),

    // Inventory
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "IV001", "재고 정보를 찾을 수 없습니다."),

    // Invoice
    INVOICE_NOT_FOUND(HttpStatus.NOT_FOUND, "IN001", "송장 정보를 찾을 수 없습니다."),

    // Transaction
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "입출고 정보를 찾을 수 없습니다."),
    TRANSACTION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "입출고 유형을 찾을 수 없습니다."),
    TRANSACTION_NOT_PENDING(HttpStatus.BAD_REQUEST, "T003", "입출고 상태가 대기 중이 아닙니다."),
    SCHEDULED_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "T004", "입출고 유형이 기타수량이 아닌 경우 예정일이 필요합니다."),
    SCHEDULED_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "T005", "입출고 유형이 기탁수량인 경우 예정일은 허용되지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
