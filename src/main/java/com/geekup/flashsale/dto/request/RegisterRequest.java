package com.geekup.flashsale.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client register request model
 *
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "user name cannot be null")
    @Valid
    private String username;

    @NotNull(message = "password cannot be null")
    @Valid
    private String password;

    @NotNull(message = "confirmPassword cannot be null")
    @Valid
    private String confirmPassword;
}
