package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthorityEntity, Long> {
    List<UserAuthorityEntity> findByUserEntity(UserEntity userEntity);
    
    @Modifying //annotation is used to indicate that the query will modify the database (in this case, a delete operation)
    @Transactional
    @Query("DELETE FROM UserAuthorityEntity uae WHERE uae.userEntity.memberId = :userId")
    void deleteByUserEntityId(Long userId);
}
