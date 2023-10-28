package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

}
