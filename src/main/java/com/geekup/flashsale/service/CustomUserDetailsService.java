package com.geekup.flashsale.service;

import com.geekup.flashsale.entity.AppUser;
import com.geekup.flashsale.repository.AppUserRepository;
import com.geekup.flashsale.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges application users to Spring Security's UserDetails model.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    /**
     * Loads a user by username for authentication.
     *
     * @param username account username
     * @return Spring Security user details
     * @throws UsernameNotFoundException when user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not identified: " + username));
        return new CustomUserDetails(user);
    }
}
