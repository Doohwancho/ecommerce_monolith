package com.cho.ecommerce.domain.member.entity;

import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ADDRESS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;  // Add this line
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
//    @SequenceGenerator(
//        name = "address_seq",
//        sequenceName = "ADDRESS_SEQ",
//        allocationSize = 1000
//    )
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "address_seq")
//    @TableGenerator(
//        name = "address_seq",
//        table = "ADDRESS_ID",
//        pkColumnName = "SEQ_NAME",
//        pkColumnValue = "ADDRESS_MAX_ID",
//        valueColumnName = "SEQ_NUMBER",
//        initialValue = 1, //초기 값
//        allocationSize = 1 //증가 값
//    )
    @Column(name = "ADDRESS_ID")
    private Long addressId;
    
    @NotBlank(message = "Street is required")
    @Column(length = DatabaseConstants.STREET_SIZE)
    private String street;
    
    @NotBlank(message = "City is required")
    @Column(length = DatabaseConstants.CITY_SIZE)
    private String city;
    
    @NotBlank(message = "State is required")
    @Column(length = DatabaseConstants.STATE_SIZE)
    private String state;
    
    @NotBlank(message = "Country is required")
    @Column(length = DatabaseConstants.COUNTRY_SIZE)
    private String country;
    
    @NotBlank(message = "Zip code is required")
    @Column(name = "zip_code", length = DatabaseConstants.ZIPCODE_SIZE)
    private String zipCode;
    
    @NotNull
    @OneToOne(mappedBy = "address")
//    @JoinColumn(name = "USER_ID")
    private UserEntity user;
}
