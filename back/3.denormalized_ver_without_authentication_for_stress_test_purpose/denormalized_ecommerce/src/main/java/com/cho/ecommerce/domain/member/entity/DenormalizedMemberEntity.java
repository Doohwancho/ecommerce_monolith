package com.cho.ecommerce.domain.member.entity;

import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DENORMALIZED_MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DenormalizedMemberEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;
    
    @NotBlank(message = "Username is required")
    @Column(name = "USER_ID")
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @JsonIgnore
    private String password;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    private boolean enabled;
    
    @Min(0)
    private Integer failedAttempt;
    
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    
    @Column(name = "CREATED_AT")
    @NotNull(message = "User must have created datetime")
    private LocalDateTime created;
    
    @Column(name = "UPDATED_AT")
    @NotNull(message = "User must have updated datetime")
    private LocalDateTime updated;
    
    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<DenormalizedOrderEntity> orders = new HashSet<>();
    
    public boolean isAccountNonExpired() {
        return enabled;
    }
    
    public boolean isAccountNonLocked() {
        return enabled;
    }
    
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
    
    @Override
    public String toString() {
        return "DenormalizedMember{" +
            "memberId=" + memberId +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", role='" + role + '\'' +
            ", enabled=" + enabled +
            ", created=" + created +
            ", updated=" + updated +
            '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(memberId, username, email, name, password, role, enabled, created, updated);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DenormalizedMemberEntity that = (DenormalizedMemberEntity) obj;
        return enabled == that.enabled &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(username, that.username) &&
            Objects.equals(email, that.email) &&
            Objects.equals(name, that.name) &&
            Objects.equals(password, that.password) &&
            Objects.equals(role, that.role) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated);
    }
}