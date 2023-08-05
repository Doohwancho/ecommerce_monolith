package com.cho.ecommerce.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="MEMBER")
@Data //TODO 2 - @Data -> custom getter/setter, toString(), etc
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails { // UserDetailService를 구현한 클래스를 따로 분리해서 만들어서 처리해도 된다.
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MEMBER_ID")
    private Long memberId;
    
    @Column(unique = true)
    private String userId;
    
    @Column(unique = true)
    private String email;
    private String name;
//    private String picUrl; //TODO 1 - user picture?
    @JsonIgnore //prevent the password from being included when the object is serialized into JSON format
    private String password;
    private String role;
    private boolean enabled;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Authority> authorities;
    
    @Column(name="CREATED_AT")
    private LocalDateTime created;
    
    @Column(name="UPDATED_AT")
    private LocalDateTime updated;
    
    @Override
    public Collection<GrantedAuthority> getAuthorities() { //TODO 7 - <? extends GrantedAuthority> 가 왜 안되는가?
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
    
}
