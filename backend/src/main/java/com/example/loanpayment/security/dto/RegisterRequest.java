package com.example.loanpayment.security.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        @NotBlank String password
) {}
