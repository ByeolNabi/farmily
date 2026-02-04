package com.ssafy.farmily.global.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
