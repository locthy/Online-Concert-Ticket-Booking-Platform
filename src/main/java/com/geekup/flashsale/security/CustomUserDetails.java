package com.geekup.flashsale.security;

import com.geekup.flashsale.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security adapter around {@link AppUser}.
 */
public class CustomUserDetails implements UserDetails {
    private final AppUser appUser;

    public CustomUserDetails(AppUser appUser) {
        this.appUser = appUser;
    }

    /**
     * Maps the persisted role to Spring's authority model.
     *
     * @return granted authorities for current user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(appUser.getRole()));
    }

    @Override
    public String getPassword() {
        return appUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return appUser.getUserName();
    }

    /**
     * These account-state flags are currently hardcoded to enabled.
     * Extend this implementation when account lifecycle policies are introduced.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public AppUser getAppUser() {
        return appUser;
    }
}
