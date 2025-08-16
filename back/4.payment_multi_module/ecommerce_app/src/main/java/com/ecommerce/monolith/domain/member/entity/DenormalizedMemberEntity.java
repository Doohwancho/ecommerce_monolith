package com.ecommerce.monolith.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @NotNull(message = "Member ID is required")
    @Column(name = "MEMBER_ID")
    private Long memberId;
    
    @NotBlank(message = "Username is required")
    @Column(name = "USER_ID")
    private String userId;
    
//    @Email(message = "Invalid email format")
    @Column(name = "EMAIL")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(name = "NAME")
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column(name = "PASSWORD")
    @JsonIgnore
    private String password;
    
    @Column(name = "ROLE")
    @NotBlank(message = "Role is required")
    private String role;
    
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Column(name = "FAILED_ATTEMPT")
    @Min(0)
    private Integer failedAttempt;
    
    @Column(name = "STREET")
    @NotBlank(message = "Street is required")
    private String street;
    
    @Column(name = "CITY")
    @NotBlank(message = "City is required")
    private String city;
    
    @Column(name = "STATE")
    @NotBlank(message = "State is required")
    private String state;
    
    @Column(name = "COUNTRY")
    @NotBlank(message = "Country is required")
    private String country;
    
    @Column(name = "ZIP_CODE")
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    
    @Column(name = "CREATED_AT")
    @NotNull(message = "User must have created datetime")
    private OffsetDateTime created;
    
    @Column(name = "UPDATED_AT")
    @NotNull(message = "User must have updated datetime")
    private OffsetDateTime updated;
    
//    @JsonIgnore
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @Builder.Default
//    private Set<DenormalizedOrderEntity> orders = new HashSet<>();
    
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
            ", userId='" + userId + '\'' +
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
        return Objects.hash(memberId, userId, email, name, password, role, enabled, created, updated);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DenormalizedMemberEntity that = (DenormalizedMemberEntity) obj;
        return enabled == that.enabled &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(email, that.email) &&
            Objects.equals(name, that.name) &&
            Objects.equals(password, that.password) &&
            Objects.equals(role, that.role) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated);
    }
}