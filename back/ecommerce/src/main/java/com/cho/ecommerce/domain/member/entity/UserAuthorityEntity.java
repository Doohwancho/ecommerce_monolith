package com.cho.ecommerce.domain.member.entity;

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
public class UserAuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private UserEntity userEntity;
    
    @ManyToOne
    @JoinColumn(name = "AUTHORITY_ID")
    private AuthorityEntity authorityEntity;
    
    public AuthorityEntity getAuthorityEntity() {
        return authorityEntity;
    }
    
    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
    public void setAuthorityEntity(AuthorityEntity authorityEntity) {
        this.authorityEntity = authorityEntity;
    }
    
    @Override
    public String toString() {
        return "UserAuthority{" +
            "id=" + id +
            ", authority=" + authorityEntity +
            '}';
    }
    
    // Implementing hashCode() and equals() without referencing user to avoid recursion
    @Override
    public int hashCode() {
        return Objects.hash(id, authorityEntity);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAuthorityEntity that = (UserAuthorityEntity) obj;
        return Objects.equals(id, that.id) &&
            Objects.equals(authorityEntity, that.authorityEntity);
    }
}
