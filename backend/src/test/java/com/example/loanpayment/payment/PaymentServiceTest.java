package com.example.loanpayment.payment;

import com.example.loanpayment.loan.LoanService;
import com.example.loanpayment.loan.dto.CreateLoanRequest;
import com.example.loanpayment.payment.dto.CreatePaymentRequest;
import com.example.loanpayment.security.AuthService;
import com.example.loanpayment.security.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {

    @Autowired AuthService authService;
    @Autowired LoanService loanService;
    @Autowired PaymentService paymentService;

    private String username;

    @BeforeEach
    void setup() {
        username = "user_" + UUID.randomUUID();

        authService.registerCustomer(new RegisterRequest(
                "John",
                "Doe",
                username,
                "password123"
        ));
    }

    @Test
    void paymentReducesBalance() {
        var auth = new TestingAuthenticationToken(username, "N/A", "ROLE_CUSTOMER");
        var loan = loanService.createLoan(new CreateLoanRequest(new BigDecimal("500.00"), 6), auth);

        var payment = paymentService.recordPayment(
                new CreatePaymentRequest(loan.getId(), new BigDecimal("125.00")),
                auth
        );

        var updated = loanService.getLoan(loan.getId());
        assertThat(payment.getId()).isNotNull();
        assertThat(updated.getRemainingBalance()).isEqualByComparingTo("375.00");
    }

    @Test
    void settlesLoanWhenPaidInFull() {
        var auth = new TestingAuthenticationToken(username, "N/A", "ROLE_CUSTOMER");
        var loan = loanService.createLoan(new CreateLoanRequest(new BigDecimal("200.00"), 3), auth);

        paymentService.recordPayment(
                new CreatePaymentRequest(loan.getId(), new BigDecimal("200.00")),
                auth
        );

        var updated = loanService.getLoan(loan.getId());
        assertThat(updated.getStatus().name()).isEqualTo("SETTLED");
        assertThat(updated.getRemainingBalance()).isEqualByComparingTo("0.00");
    }
}