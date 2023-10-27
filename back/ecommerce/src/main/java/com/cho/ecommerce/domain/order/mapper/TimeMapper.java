package com.cho.ecommerce.domain.order.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.apache.tomcat.jni.Local;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimeMapper {
    LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime);
    OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime);
    
    
}