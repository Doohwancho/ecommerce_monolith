package com.cho.ecommerce.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_AUTHORITY")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "AUTHORITY_ID")
    private Authority authority;
    
    public Authority getAuthority() {
        return authority;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    
    @Override
    public String toString() {
        return "UserAuthority{" +
            "id=" + id +
            ", authority=" + authority +
            '}';
    }
    
    // Implementing hashCode() and equals() without referencing user to avoid recursion
    @Override
    public int hashCode() {
        return Objects.hash(id, authority);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAuthority that = (UserAuthority) obj;
        return Objects.equals(id, that.id) &&
            Objects.equals(authority, that.authority);
    }
}
