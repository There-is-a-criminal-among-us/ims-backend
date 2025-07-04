package kr.co.ksgk.ims.global.error.exception;

import kr.co.ksgk.ims.global.error.ErrorCode;

public class ConflictException extends BusinessException {
    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}