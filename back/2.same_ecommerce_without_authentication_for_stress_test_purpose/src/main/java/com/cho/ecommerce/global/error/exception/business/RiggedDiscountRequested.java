package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class RiggedDiscountRequested extends BusinessException {
    
    private static final long serialVersionUID = 1L;
    
    public RiggedDiscountRequested(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public RiggedDiscountRequested(String message) {
        super(message, ErrorCode.INVALID_DISCOUNT);
    }
    
    public RiggedDiscountRequested(ErrorCode errorCode) {
        super(errorCode);
    }
}