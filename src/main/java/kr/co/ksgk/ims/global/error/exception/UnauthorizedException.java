package kr.co.ksgk.ims.global.error.exception;

import kr.co.ksgk.ims.global.error.ErrorCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}