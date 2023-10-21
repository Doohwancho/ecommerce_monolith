package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthorityEntity, Long> {
    List<UserAuthorityEntity> findByUserEntity(UserEntity userEntity);
}
