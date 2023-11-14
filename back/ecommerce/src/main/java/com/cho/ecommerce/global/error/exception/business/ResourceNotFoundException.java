package com.cho.ecommerce.global.error.exception.business;

import com.cho.ecommerce.global.error.ErrorCode;

//businessException에 있던 EntityNotFound를 보안 이유로 ResourceNotFoundException으로 바꿔서 common/으로 옮김

public class ResourceNotFoundException extends BusinessException {
    
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
    public ResourceNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
    
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
