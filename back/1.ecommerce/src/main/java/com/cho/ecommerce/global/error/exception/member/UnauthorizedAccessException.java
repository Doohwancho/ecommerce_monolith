package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class UnauthorizedAccessException extends MemberException {
    
    public UnauthorizedAccessException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public UnauthorizedAccessException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_VERIFICATION_ATTEMPT);
    }
    
    public UnauthorizedAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
