package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.InactiveMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InactiveMemberRepository extends JpaRepository<InactiveMemberEntity, Long> {
}

