package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.QAddressEntity;
import com.cho.ecommerce.domain.member.entity.QAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Optional<UserEntity> findUserDetailsByUsername(String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        
        QUserEntity user = QUserEntity.userEntity;
        QAddressEntity address = QAddressEntity.addressEntity;
        QUserAuthorityEntity userAuthority = QUserAuthorityEntity.userAuthorityEntity;
        QAuthorityEntity authority = QAuthorityEntity.authorityEntity;
        
        // First, fetch the user with address
        UserEntity result = queryFactory
            .selectFrom(user)
            .leftJoin(user.address, address).fetchJoin()
            .where(user.username.eq(username))
            .fetchFirst(); //the query will return the first matching user, even if there are multiple users with the same username. This should resolve the NonUniqueResultException.
        
        if (result != null) {
            // Then, fetch authorities separately
            List<UserAuthorityEntity> authorities = queryFactory
                .selectFrom(userAuthority)
                .join(userAuthority.authorityEntity, authority).fetchJoin()
                .where(userAuthority.userEntity.eq(result))
                .fetch();
            
            // Set the fetched authorities
            result.setUserAuthorities(new HashSet<>(authorities));
        }
        
        return Optional.ofNullable(result);
    }
}
