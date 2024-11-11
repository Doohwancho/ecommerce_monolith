package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.InactiveMemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InactiveMemberRepository extends JpaRepository<InactiveMemberEntity, Long> {
    
    Optional<InactiveMemberEntity> findByUsername(String userId);
}

