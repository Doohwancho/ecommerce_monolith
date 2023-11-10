package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

//attempting to withdraw an amount that exceeds the account balance
public class InsufficientFundsException extends BusinessException {
    private static final long serialVersionUID = 1L;
    
    public InsufficientFundsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public InsufficientFundsException(String message) {
        super(message, ErrorCode.USER_ALREADY_EXIST);
    }
    
    public InsufficientFundsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
