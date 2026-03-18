package com.example.loanpayment.dashboard;

import com.example.loanpayment.loan.LoanMapper;
import com.example.loanpayment.loan.LoanService;
import com.example.loanpayment.payment.PaymentMapper;
import com.example.loanpayment.payment.PaymentService;
import com.example.loanpayment.security.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final LoanService loanService;
    private final LoanMapper loanMapper;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public DashboardController(UserRepository userRepository, LoanService loanService, LoanMapper loanMapper,
                               PaymentService paymentService, PaymentMapper paymentMapper) {
        this.userRepository = userRepository;
        this.loanService = loanService;
        this.loanMapper = loanMapper;
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @GetMapping
    public DashboardResponse myDashboard(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        var loans = loanService.myLoans(auth).stream().map(loanMapper::toResponse).toList();
        return new DashboardResponse(user.getUsername(), user.getFirstName(), user.getLastName(), loans);
    }

    @GetMapping("/loan/{loanId}/payments")
    public DashboardPaymentsResponse paymentsForLoan(@PathVariable UUID loanId) {
        var payments = paymentService.listForLoan(loanId).stream().map(paymentMapper::toResponse).toList();
        return new DashboardPaymentsResponse(loanId, payments);
    }
}
