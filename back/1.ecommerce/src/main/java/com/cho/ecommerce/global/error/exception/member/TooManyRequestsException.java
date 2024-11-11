package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class TooManyRequestsException extends MemberException {
    
    public TooManyRequestsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public TooManyRequestsException(String message) {
        super(message, ErrorCode.TOO_MANY_REQUESTS);
    }
    
    public TooManyRequestsException(ErrorCode errorCode) {
        super(errorCode);
    }
}