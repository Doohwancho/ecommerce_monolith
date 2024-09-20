package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class RequestedOrderQuantityExceedsCurrentStock extends BusinessException {
    
    private static final long serialVersionUID = 1L;
    
    public RequestedOrderQuantityExceedsCurrentStock(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public RequestedOrderQuantityExceedsCurrentStock(String message) {
        super(message, ErrorCode.REQUESTED_QUANTITY_EXCEEDS_STOCK);
    }
    
    public RequestedOrderQuantityExceedsCurrentStock(ErrorCode errorCode) {
        super(errorCode);
    }
}