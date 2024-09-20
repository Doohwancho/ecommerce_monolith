package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

//redis에 있던 user's session을 invalidate 하려는 시도가 실패할 때 던지는 Exception
public class InvalidatingSessionForUser extends MemberException{

    public InvalidatingSessionForUser(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public InvalidatingSessionForUser(String message) {
        super(message, ErrorCode.INVALIDATING_SESSION_FOR_USER);
    }
    
    public InvalidatingSessionForUser(ErrorCode errorCode) {
        super(errorCode);
    }
}
