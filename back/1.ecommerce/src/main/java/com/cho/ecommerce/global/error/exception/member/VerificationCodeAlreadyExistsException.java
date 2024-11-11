package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class VerificationCodeAlreadyExistsException extends MemberException {
    
    public VerificationCodeAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public VerificationCodeAlreadyExistsException(String message) {
        super(message, ErrorCode.VERIFICATION_CODE_ALREADY_EXISTS);
    }
    
    public VerificationCodeAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

