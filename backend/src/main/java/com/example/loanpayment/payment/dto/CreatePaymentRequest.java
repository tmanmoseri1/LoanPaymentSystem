package com.example.loanpayment.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID loanId,
        @NotNull BigDecimal amount
) {}
