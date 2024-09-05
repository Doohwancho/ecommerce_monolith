package com.cho.ecommerce.global.error.exception.member;

import com.cho.ecommerce.global.error.ErrorCode;

//account lock된 유저가 authenticate 할 때 던지는 exception
public class LockedAccountUserFailedToAuthenticate extends MemberException {
    
    private static final long serialVersionUID = 1L;
    
    public LockedAccountUserFailedToAuthenticate(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public LockedAccountUserFailedToAuthenticate(String message) {
        super(message, ErrorCode.USER_ALREADY_EXIST);
    }
    
    public LockedAccountUserFailedToAuthenticate(ErrorCode errorCode) {
        super(errorCode);
    }
}