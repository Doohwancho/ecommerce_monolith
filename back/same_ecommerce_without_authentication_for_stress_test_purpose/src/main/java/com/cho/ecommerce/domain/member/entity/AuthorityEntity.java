package com.cho.ecommerce.domain.member.entity;


import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AUTHORITY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthorityEntity {
    
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final AuthorityEntity USER = new AuthorityEntity(ROLE_USER);
    public static final AuthorityEntity ADMIN = new AuthorityEntity(ROLE_ADMIN);
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq")
//    @SequenceGenerator(
//        name = "authority_seq",
//        sequenceName = "AUTHORITY_SEQ",
//        allocationSize = 1
//    )
    @Column(name = "AUTHORITY_ID")
    private Long id;
    
    @NotBlank(message = "Authority is required")
    @Column(length = DatabaseConstants.AUTHORITY_SIZE)
    private String authority;
    
    public AuthorityEntity(String authority) {
        this.authority = authority;
    }
    
//    @Override
    public String getAuthority() {
        return authority;
    }
    
    @Override
    public String toString() {
        return "Authority{" +
            "id=" + id +
            ", authority='" + authority + '\'' +
            '}';
    }
    
    // Implementing hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(id, authority);
    }
    
    // Implementing equals()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AuthorityEntity authorityEntity1 = (AuthorityEntity) obj;
        return Objects.equals(id, authorityEntity1.id) &&
            Objects.equals(authority, authorityEntity1.authority);
    }
}
