package com.cho.ecommerce.global.config.security.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GeoIp2Config {
    
    @Bean
    public DatabaseReader databaseReader() throws IOException, GeoIp2Exception {
        ClassPathResource resource = new ClassPathResource("GeoLite2-Country.mmdb");
        return new DatabaseReader.Builder(resource.getInputStream()).build();
    }
}
