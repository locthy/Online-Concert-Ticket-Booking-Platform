package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for application users.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by unique username.
     *
     * @param userName username
     * @return optional user
     */
    Optional<AppUser> findByUserName(String userName);
}
