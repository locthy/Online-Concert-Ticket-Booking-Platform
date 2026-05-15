package com.geekup.flashsale.service;

import com.geekup.flashsale.dto.request.LoginRequest;
import com.geekup.flashsale.entity.AppUser;
import com.geekup.flashsale.exception.IncorrectPasswordException;
import com.geekup.flashsale.exception.UserNameNotExistsException;
import com.geekup.flashsale.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handle login logic for username and password
 *
 * */
@AllArgsConstructor
@Service
public class LoginService {
    private final AppUserRepository appUserRepository;

    private final PasswordEncoder encoder;

    /**
     * Login user account
     *
     * @param request a DTO login request
     * @return a success login string
     * @throws UserNameNotExistsException when a username is not exists
     * @throws IncorrectPasswordException when logging in with incorrect password
     *
     * */
    public String loginUser(LoginRequest request) {
        AppUser user = appUserRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new UserNameNotExistsException("Username does not exists"));

        if(!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IncorrectPasswordException("Incorrect Password");
        }
        return "Login success!";
    }

}
