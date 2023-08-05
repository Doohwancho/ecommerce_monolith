package com.cho.ecommerce.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_AUTHORITY")
@Data
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
    
}
