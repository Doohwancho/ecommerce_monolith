package com.cho.ecommerce.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data //TODO 2 - @Data -> custom getter/setter, toString(), etc
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authority implements GrantedAuthority {
    
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final Authority USER = new Authority(ROLE_USER);
    public static final Authority ADMIN = new Authority(ROLE_ADMIN);
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AUTHORITY_ID")
    private Long id;
    
    private String authority;
    
    public Authority(String authority) {
        this.authority = authority;
    }
    
    @Override
    public String getAuthority() {
        return authority;
    }
}
