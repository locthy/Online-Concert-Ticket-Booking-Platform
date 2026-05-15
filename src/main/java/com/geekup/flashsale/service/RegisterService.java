package com.geekup.flashsale.service;

import com.geekup.flashsale.dto.request.RegisterRequest;
import com.geekup.flashsale.entity.AppUser;
import com.geekup.flashsale.exception.ConfirmPasswordNotMatchException;
import com.geekup.flashsale.exception.UsernameAlreadyExistsException;
import com.geekup.flashsale.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handle register logic for username, password and confirm password
 *
 **/
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final AppUserRepository appUserRepository;

    private final PasswordEncoder encoder;

    /**
     * Register user account, ensure strict conditions
     *
     * @param request a DTO register request
     * @return Success register String
     * @throws ConfirmPasswordNotMatchException when confirm password does not match
     * @throws UsernameAlreadyExistsException when a username already exists
     * */
    public String registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ConfirmPasswordNotMatchException("Confirm password does not match");
        }

        appUserRepository.findByUserName(request.getUsername())
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException("Username already exists");
                });
        AppUser appUser = new AppUser();
        appUser.setUserName(request.getUsername());
        appUser.setPasswordHash(encoder.encode(request.getPassword()));
        appUser.setRole("user");
        appUserRepository.save(appUser);
        return "Register success";
    }
}
