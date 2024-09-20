package com.cho.ecommerce.global.error.exception.member;


import com.cho.ecommerce.global.error.ErrorCode;

//if a user tries to register with an email that is already in use
public class UserAlreadyExistsException extends MemberException {
    
    private static final long serialVersionUID = 1L;
    
    public UserAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public UserAlreadyExistsException(String message) {
        super(message, ErrorCode.USER_ALREADY_EXIST);
    }
    
    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

