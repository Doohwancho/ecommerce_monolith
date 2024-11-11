package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class InvalidPasswordException extends MemberException {
    
    public InvalidPasswordException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public InvalidPasswordException(String message) {
        super(message, ErrorCode.INVALID_PASSWORD);
    }
    
    public InvalidPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
