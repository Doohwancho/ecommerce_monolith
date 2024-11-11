package com.cho.ecommerce.global.error;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    
    // Common
    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
    RESOURCE_NOT_FOUND(404, "C003", " Resource Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),
    INVALID_TYPE_VALUE(400, "C005", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),
    TOO_MANY_REQUESTS(400, "C007", "Too Many Requests"),
    
    // Business
    
    // Member
    EMAIL_DUPLICATION(400, "M001", "Email is Duplication"),
    LOGIN_INPUT_INVALID(400, "M002", "Login input is invalid"),
    USER_ALREADY_EXIST(400, "M003", "User already exists"),
    LOCKED_USER_FAILED_TO_AUTHENTICATE(403, "M004",
        "locked account user tried to authenticate and failed"),
    INVALIDATING_SESSION_FOR_USER(500, "M005", "Error invalidating sessions for user"),
    DUPLICATE_USERS(400, "M006", "Duplicate User already exists with the same id"),
    
    // Member/Verification
    VERIFICATION_ERROR(400, "V001", "Error verifying User"),
    VERIFICATION_CODE_ALREADY_EXISTS(400, "V002",
        "Server generated Verification Code but it already exists"),
    EXCEEDS_MAX_VERIFICATION_ATTEMPTS(400, "V003", "verification attempts exceeds maximum amounts"),
    UNAUTHORIZED_VERIFICATION_ATTEMPT(400, "V004", "unauthorized verification attempts"),
    INVALID_PASSWORD(400, "V005", "Invalid Password"),
    
    // Order
    INVALID_ORDER_REQUEST(400, "O001", "Invalid Order Request"),
    EMPTY_ORDER_ITEMS(400, "O002", "Order was requested but orderItems were not included"),
    ORDER_REQUESTED_BY_INVALID_USER(400, "O003", "order was requested by invalid user"),
    ORDER_ITEMS_REQUESTED_BY_MORE_THAN_ONE_USER(400, "O004",
        "order with a list of order items was requested by more than 1 user"),
    REQUESTED_QUANTITY_EXCEEDS_STOCK(400, "O005",
        "requested order amount exceeds current stock quantity"),
    
    
    // Discount
    INVALID_DISCOUNT(400, "D001",
        "value of requested discount are not valid in comparison to saved discount on database")
    
    // Coupon
//    COUPON_ALREADY_USE(400, "CO001", "Coupon was already used"),
//    COUPON_EXPIRE(400, "CO002", "Coupon was already expired")
    
    ;
    private final String code;
    private final String message;
    private int status;
    
    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getCode() {
        return code;
    }
    
    public int getStatus() {
        return status;
    }
}
