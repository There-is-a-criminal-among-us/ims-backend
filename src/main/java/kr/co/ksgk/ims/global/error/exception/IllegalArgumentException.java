package kr.co.ksgk.ims.global.error.exception;

import kr.co.ksgk.ims.global.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {
    public IllegalArgumentException() {
        super(ErrorCode.BAD_REQUEST);
    }
    public IllegalArgumentException(ErrorCode errorCode) {super(errorCode);}
}
