package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class InvalidOrderRequest extends BusinessException{
    private static final long serialVersionUID = 1L;
    
    public InvalidOrderRequest(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public InvalidOrderRequest(String message) {
        super(message, ErrorCode.INVALID_ORDER_REQUEST);
    }
    
    public InvalidOrderRequest(ErrorCode errorCode) {
        super(errorCode);
    }
}
