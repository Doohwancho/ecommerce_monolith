package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

//when POST /orders was requested, but orderItems were not included
public class OrderItemsNotIncluded extends BusinessException {
    private static final long serialVersionUID = 1L;
    
    public OrderItemsNotIncluded(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public OrderItemsNotIncluded(String message) {
        super(message, ErrorCode.EMPTY_ORDER_ITEMS);
    }
    
    public OrderItemsNotIncluded(ErrorCode errorCode) {
        super(errorCode);
    }
}
