package com.cho.ecommerce.domain.member.domain;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    
    private Long memberId;
    private String username;
    private String email;
    private String name;
    private Address address;
    private String role;
    private Boolean enabled;
    private Integer failedAttempt;
    private OffsetDateTime created;
    private OffsetDateTime updated;
    private List<String> authorities;
    
    public static Boolean isLockedUser(UserEntity user) {
        return !user.isAccountNonLocked();
    }
    
    public User(Builder builder) {
        this.memberId = builder.memberId;
        this.username = builder.username;
        this.email = builder.email;
        this.name = builder.name;
        this.address = builder.address;
        this.role = builder.role;
        this.enabled = builder.enabled;
        this.failedAttempt = builder.failedAttempt;
        this.created = builder.created;
        this.updated = builder.updated;
        this.authorities = builder.authorities;
    }
    
    // Getters and setters
    
    public static class Builder {
        
        private Long memberId;
        private String username;
        private String email;
        private String name;
        private Address address;
        private String role;
        private Boolean enabled;
        private Integer failedAttempt;
        private OffsetDateTime created;
        private OffsetDateTime updated;
        private List<String> authorities;
        
        public Builder() {
            this.authorities = new ArrayList<>();
        }
        
        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder address(Address address) {
            this.address = address;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        
        public Builder failedAttempt(Integer failedAttempt) {
            this.failedAttempt = failedAttempt;
            return this;
        }
        
        public Builder created(OffsetDateTime created) {
            this.created = created;
            return this;
        }
        
        public Builder updated(OffsetDateTime updated) {
            this.updated = updated;
            return this;
        }
        
        public Builder authorities(List<String> authorities) {
            this.authorities = authorities;
            return this;
        }
        
        public Builder addAuthority(String authority) {
            this.authorities.add(authority);
            return this;
        }
        
        public User build() {
            assert memberId != null : "Member ID is required";
            assert username != null && !username.trim().isEmpty() : "Username is required";
            assert email != null && email.contains("@") : "Email must be a valid email address";
            assert address != null : "Address is required";
            assert role != null && !role.trim().isEmpty() : "Role is required";
            assert enabled != null : "Enabled status is required";
            assert created != null : "Creation date is required";
            assert updated != null : "Updated date is required";
            assert authorities != null : "Authorities are required";
            
            return new User(this);
        }
    }
}

