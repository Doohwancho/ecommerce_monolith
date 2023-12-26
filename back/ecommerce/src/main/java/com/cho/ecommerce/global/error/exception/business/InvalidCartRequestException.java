package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class InvalidCartRequestException extends BusinessException{
    private static final long serialVersionUID = 1L;
    
    public InvalidCartRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public InvalidCartRequestException(String message) {
        super(message, ErrorCode.INVALID_CART_REQUEST);
    }
    
    public InvalidCartRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
