package com.example.loanpayment.dashboard;

import com.example.loanpayment.payment.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public record DashboardPaymentsResponse(
        UUID loanId,
        List<PaymentResponse> payments
) {}
