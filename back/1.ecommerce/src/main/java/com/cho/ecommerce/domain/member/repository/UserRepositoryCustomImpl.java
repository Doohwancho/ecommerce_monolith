package com.cho.ecommerce.domain.member.repository;

import com.cho.ecommerce.domain.member.entity.QAddressEntity;
import com.cho.ecommerce.domain.member.entity.QAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.QUserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.global.error.exception.member.DuplicateUsernameException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Optional<UserEntity> findUserDetailsByUsername(String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        
        QUserEntity user = QUserEntity.userEntity;
        QAddressEntity address = QAddressEntity.addressEntity;
        QUserAuthorityEntity userAuthority = QUserAuthorityEntity.userAuthorityEntity;
        QAuthorityEntity authority = QAuthorityEntity.authorityEntity;
        
        try {
            
            // First, fetch the user with address
            List<UserEntity> results = queryFactory
                .selectFrom(user)
                .leftJoin(user.address, address).fetchJoin()
                .where(user.username.eq(username))
                .fetch();
//            .fetchFirst(); //the query will return the first matching user, even if there are multiple users with the same username. This should resolve the NonUniqueResultException.
            
            // Handle potential duplicates
            if (results.size() > 1) {
                log.error("Multiple users found with username: {}. Count: {}", username,
                    results.size());
                throw new DuplicateUsernameException(
                    "Multiple accounts found with username: " + username);
            }
            
            UserEntity result = results.isEmpty() ? null : results.get(0);
            
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
        } catch (DuplicateUsernameException e) {
            throw e;  // Re-throw our custom exception
        } catch (NonUniqueResultException e) {
            log.error("Unexpected duplicate users found for username: {}", username);
            throw new DuplicateUsernameException(
                "Multiple accounts found with username: " + username);
        } catch (Exception e) {
            log.error("Error fetching user details for username: {}", username, e);
            throw new RuntimeException("Error fetching user details", e);
        }
    }
}
