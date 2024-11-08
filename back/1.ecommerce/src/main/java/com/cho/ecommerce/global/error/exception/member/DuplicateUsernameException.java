package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

public class DuplicateUsernameException extends MemberException {
    
    public DuplicateUsernameException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public DuplicateUsernameException(String message) {
        super(message, ErrorCode.DUPLICATE_USERS);
    }
    
    public DuplicateUsernameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
