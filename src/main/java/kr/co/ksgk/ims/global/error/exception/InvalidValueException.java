package kr.co.ksgk.ims.global.error.exception;

import kr.co.ksgk.ims.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
        super(ErrorCode.BAD_REQUEST);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}