package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class OrderRequestByInvalidUser extends BusinessException {
    private static final long serialVersionUID = 1L;
    
    public OrderRequestByInvalidUser(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public OrderRequestByInvalidUser(String message) {
        super(message, ErrorCode.ORDER_REQUESTED_BY_INVALID_USER);
    }
    
    public OrderRequestByInvalidUser(ErrorCode errorCode) {
        super(errorCode);
    }
}
