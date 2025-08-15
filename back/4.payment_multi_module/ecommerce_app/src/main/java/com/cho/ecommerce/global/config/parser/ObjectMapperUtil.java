package com.cho.ecommerce.global.config.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperUtil {
    private static ObjectMapper objectMapper;
    
    public ObjectMapperUtil(ObjectMapper objectMapper) {
        ObjectMapperUtil.objectMapper = objectMapper;
        configureObjectMapper();
    }
    
    private void configureObjectMapper() {
        //1. Product.DiscountDTO에 OffsetDateTime (startDate, endDate) 파싱을 위한 설정
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        //2. List<Product.OptionDTO>, List<Product.DiscountDTO>가 String으로 되어있는걸 파싱하기 위한 설정
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
    
    //Entity class의 경우, Bean이 아니다 보니 ObjectMapper config class를 만들고 @Autowired 방식으로 주입이 안됨.
    //따라서 static method 방식으로 objectMapper를 끌어와서 사용함
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
