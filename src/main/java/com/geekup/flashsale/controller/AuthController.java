package com.geekup.flashsale.controller;


import com.geekup.flashsale.dto.request.LoginRequest;
import com.geekup.flashsale.dto.request.RegisterRequest;
import com.geekup.flashsale.service.LoginService;
import com.geekup.flashsale.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Route register and log in request to their services and response to client
 *
 * */
@Slf4j
@RequestMapping("api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    final private RegisterService registerService;
    final private LoginService loginService;

    /**
     * Route request to register service
     *
     * @param request a DTO register request
     * @return a response entity
     * */
    @PostMapping("/register")
    public ResponseEntity<String> accountRegister(
            @Valid @RequestBody RegisterRequest request) {
        log.info("User {} register", request.getUsername());
        String response = registerService.registerUser(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Route request to login service
     *
     * @param request a DTO login request
     * @return a response entity
     * */
    @PostMapping("/login")
    public ResponseEntity<String> accountLogin(
            @Valid @RequestBody LoginRequest request) {
        log.info("User {} Login", request.getUsername());
        String response = loginService.loginUser(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
