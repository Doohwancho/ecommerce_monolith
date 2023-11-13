package com.cho.ecommerce.domain.member.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    
    private Long addressId;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
