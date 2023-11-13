package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserEntity> findByUsername(String username);

}
