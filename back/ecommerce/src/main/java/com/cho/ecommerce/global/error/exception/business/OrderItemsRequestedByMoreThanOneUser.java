package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

public class OrderItemsRequestedByMoreThanOneUser extends BusinessException {
    private static final long serialVersionUID = 1L;
    
    public OrderItemsRequestedByMoreThanOneUser(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public OrderItemsRequestedByMoreThanOneUser(String message) {
        super(message, ErrorCode.ORDER_ITEMS_REQUESTED_BY_MORE_THAN_ONE_USER);
    }
    
    public OrderItemsRequestedByMoreThanOneUser(ErrorCode errorCode) {
        super(errorCode);
    }
    
}
