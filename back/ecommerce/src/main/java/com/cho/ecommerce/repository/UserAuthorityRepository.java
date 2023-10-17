package com.cho.ecommerce.repository;

import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.domain.UserAuthority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    List<UserAuthority> findByUser(User user);
}
