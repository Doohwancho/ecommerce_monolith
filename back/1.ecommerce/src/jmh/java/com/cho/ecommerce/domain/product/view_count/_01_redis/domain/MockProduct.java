package com.cho.ecommerce.domain.product.view_count._01_redis.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Setter
@Getter
public class MockProduct implements Serializable { //redis에 json으로 serialize 해서 저장해서 필요
    
    private static final long serialVersionUID = 1L; //redis에 json으로 serialize 해서 저장해서 필요
    
    //product
    private Long productId;
    private String name;
}

