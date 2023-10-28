package com.cho.ecommerce.domain.member.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ADDRESS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ADDRESS_ID")
    private Long addressId;
    
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    @OneToOne(mappedBy = "address")
    private UserEntity user;
}
