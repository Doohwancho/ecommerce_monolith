package com.cho.ecommerce.global.config.database;

public enum DatabaseConstants {
    ;
    
    //MemberEntity.java
    public static final int MEMBER_USERNAME_SIZE = 30;
    public static final int EMAIL_SIZE = 100;
    public static final int MEMBER_NAME_SIZE = 100;
    public static final int PASSWORD_SIZE = 255;
    public static final int ROLE_SIZE = 20;
    
    
    //AddressEntity.java
    public static final int STREET_SIZE = 255;
    public static final int CITY_SIZE = 100;
    public static final int STATE_SIZE = 100;
    public static final int COUNTRY_SIZE = 100;
    public static final int ZIPCODE_SIZE = 20;
    
    //AuthorityEntity.java
    public static final int AUTHORITY_SIZE = 50;
    
    
    //ProductEntity.java
    public static final int PRODUCT_NAME_SIZE = 100;
    public static final int PRODUCT_DESCRIPTION_SIZE = 255;
    public static final int PRODUCT_RATING_SIZE = 22;
    public static final int PRODUCT_RATING_COUNT_SIZE = 11;
    
    //CategoryEntity.java
    public static final int CATEGORY_CODE_SIZE = 50;
    public static final int CATEGORY_NAME_SIZE = 100;
    
    //OptionEntity.java
    public static final int OPTION_VALUE_SIZE = 100;
    
    //OptionVariationEntity.java
    public static final int OPTION_VARIATION_VALUE_SIZE = 100;
    
    //ProductItem.java
    public static final int PRODUCT_ITEM_QUANTITY_SIZE = 11;
    public static final int PRODUCT_ITEM_PRICE_SIZE = 22;
    
    //DiscountEntity.java
    public static final int DISCOUNT_TYPE_SIZE = 50;
    public static final int DISCOUNT_VALUE_SIZE = 22;
    
    
    //Order.java
    public static final int ORDER_STATUS_SIZE = 50;
    
    
    private DatabaseConstants() {
        // Private constructor to prevent instantiation
    }
}
