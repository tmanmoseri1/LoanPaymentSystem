package com.example.loanpayment.loan.dto;

import com.example.loanpayment.loan.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        BigDecimal originalAmount,
        int termMonths,
        BigDecimal remainingBalance,
        LoanStatus status,
        Instant createdAt
) {}
