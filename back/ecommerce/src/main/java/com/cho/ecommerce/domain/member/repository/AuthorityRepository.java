package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
    Optional<AuthorityEntity> findByAuthority(String authority);
}
