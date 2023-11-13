package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.QAddressEntity;
import com.cho.ecommerce.domain.member.entity.QAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        
        QUserEntity user = QUserEntity.userEntity;
        QAddressEntity address = QAddressEntity.addressEntity;
        QUserAuthorityEntity userAuthority = QUserAuthorityEntity.userAuthorityEntity;
        QAuthorityEntity authority = QAuthorityEntity.authorityEntity;
        
        UserEntity result = queryFactory
            .selectFrom(user)
            .leftJoin(user.address, address).fetchJoin()
            .leftJoin(user.userAuthorities, userAuthority).fetchJoin()
            .leftJoin(userAuthority.authorityEntity, authority).fetchJoin()
            .where(user.username.eq(username))
            .fetchOne();
        
        return Optional.ofNullable(result);
    }
}
