package com.example.loanpayment.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID loanId,
        BigDecimal amount,
        Instant createdAt
) {}
