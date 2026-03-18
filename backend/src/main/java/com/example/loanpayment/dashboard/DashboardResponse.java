package com.example.loanpayment.dashboard;

import com.example.loanpayment.loan.dto.LoanResponse;

import java.util.List;

public record DashboardResponse(
        String username,
        String firstName,
        String lastName,
        List<LoanResponse> loans
) {}
