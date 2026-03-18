package com.example.loanpayment.loan;

import com.example.loanpayment.loan.dto.CreateLoanRequest;
import com.example.loanpayment.security.AuthService;
import com.example.loanpayment.security.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LoanServiceTest {

    @Autowired LoanService loanService;
    @Autowired AuthService authService;

    @BeforeEach
    void setup() {
        authService.registerCustomer(new RegisterRequest("John","Doe","john","pass123"));
    }

    @Test
    void createsLoanSuccessfully() {
        var auth = new TestingAuthenticationToken("john", "N/A", "ROLE_CUSTOMER");

        Loan loan = loanService.createLoan(new CreateLoanRequest(new BigDecimal("1000.00"), 12), auth);

        assertThat(loan.getId()).isNotNull();
        assertThat(loan.getRemainingBalance()).isEqualByComparingTo("1000.00");
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }
}
