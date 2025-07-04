package kr.co.ksgk.ims.global.error.exception;

import kr.co.ksgk.ims.global.error.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}