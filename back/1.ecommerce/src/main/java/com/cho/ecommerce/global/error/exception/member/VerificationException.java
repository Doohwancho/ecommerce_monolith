package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class VerificationException extends MemberException {
    
    public VerificationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public VerificationException(String message) {
        super(message, ErrorCode.VERIFICATION_ERROR);
    }
    
    public VerificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}