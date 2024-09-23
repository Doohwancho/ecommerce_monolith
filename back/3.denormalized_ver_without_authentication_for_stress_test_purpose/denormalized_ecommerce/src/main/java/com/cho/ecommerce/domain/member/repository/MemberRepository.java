package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.DenormalizedMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<DenormalizedMemberEntity, Long>,
    QuerydslPredicateExecutor<DenormalizedMemberEntity> {
    DenormalizedMemberEntity findByUserId(String username);
}