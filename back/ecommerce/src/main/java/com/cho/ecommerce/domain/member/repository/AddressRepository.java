package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    
    @Query("SELECT a FROM AddressEntity a WHERE a.user.memberId = :userId")
    AddressEntity findByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM AddressEntity a WHERE a.addressId = :addressId")
    void deleteById(Long addressId);
}
