package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
    
    Optional<UserEntity> findByEmail(String email);
    
    @Query("SELECT u FROM UserEntity u")
    List<UserEntity> findAll();
    
    Optional<UserEntity> findByUsername(String username);
    
    @Query(
        "SELECT CASE WHEN COUNT(u) > 0 OR COUNT(i) > 0 THEN true ELSE false END " +
            "FROM UserEntity u, InactiveMemberEntity i " +
            "WHERE u.username = :username OR i.username = :username")
    Boolean existsByUsername(@Param("username") String username);
}
