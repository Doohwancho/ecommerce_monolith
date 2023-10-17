package com.cho.ecommerce.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="MEMBER")
//@Data //TODO 2 - @Data -> custom getter/setter, toString(), etc
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails { // UserDetailService를 구현한 클래스를 따로 분리해서 만들어서 처리해도 된다.
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MEMBER_ID")
    private Long memberId;
    
    @Column(unique = true, name = "USER_ID")
    private String userId;
    
    @Column(unique = true)
    private String email;
    private String name;
//    private String picUrl; //TODO 1 - user picture?
    @JsonIgnore //prevent the password from being included when the object is serialized into JSON format
    private String password;
    private String role;
    private boolean enabled; //attribute is a flag to indicate the user's status. If enabled is true, the user can log in. If enabled is false, the user cannot log in. This is useful in scenarios where you might want to temporarily (or permanently) deactivate a user's account without deleting it.
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserAuthority> userAuthorities;
    
    @Column(name="CREATED_AT")
    private LocalDateTime created;
    
    @Column(name="UPDATED_AT")
    private LocalDateTime updated;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //TODO 7 - <? extends GrantedAuthority> 가 왜 안되는가?
        if (userAuthorities == null || userAuthorities.isEmpty()) {
            return Collections.emptyList();
        }
        return userAuthorities.stream()
            .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthority().getAuthority()))
            .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }
    
    //use-case)
    //a user might be locked out after several failed login attempts
    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }
    
    //Indicates whether the user's credentials (password) have expired.
    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    
    public Set<UserAuthority> getUserAuthorities() {
        return this.userAuthorities;
    }
    
    public Set<UserAuthority> setUserAuthorities(Set set) {
        return this.userAuthorities = set;
    }
    
    public String getUserId(){
        return this.userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
    
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "User{" +
            "memberId=" + memberId +
            ", userId='" + userId + '\'' +
            ", email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", password='" + password + '\'' +
            ", role='" + role + '\'' +
            ", enabled=" + enabled +
            ", created=" + created +
            ", updated=" + updated +
            '}';
    }
    
    // Implementing hashCode() and equals() without referencing userAuthorities to avoid recursion
    // You can use Objects.hash() and Objects.equals() for simplicity
    @Override
    public int hashCode() {
        return Objects.hash(memberId, userId, email, name, password, role, enabled, created, updated);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return enabled == user.enabled &&
            Objects.equals(memberId, user.memberId) &&
            Objects.equals(userId, user.userId) &&
            Objects.equals(email, user.email) &&
            Objects.equals(name, user.name) &&
            Objects.equals(password, user.password) &&
            Objects.equals(role, user.role) &&
            Objects.equals(created, user.created) &&
            Objects.equals(updated, user.updated);
    }
}
