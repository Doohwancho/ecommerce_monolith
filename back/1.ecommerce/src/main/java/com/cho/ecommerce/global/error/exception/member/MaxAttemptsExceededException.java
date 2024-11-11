package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class MaxAttemptsExceededException extends MemberException {
    
    public MaxAttemptsExceededException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public MaxAttemptsExceededException(String message) {
        super(message, ErrorCode.EXCEEDS_MAX_VERIFICATION_ATTEMPTS);
    }
    
    public MaxAttemptsExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
