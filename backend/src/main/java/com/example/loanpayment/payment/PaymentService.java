package com.example.loanpayment.payment;

import com.example.loanpayment.common.BadRequestException;
import com.example.loanpayment.common.NotFoundException;
import com.example.loanpayment.loan.Loan;
import com.example.loanpayment.loan.LoanRepository;
import com.example.loanpayment.loan.LoanStatus;
import com.example.loanpayment.payment.dto.CreatePaymentRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentNotificationPublisher publisher;

    public PaymentService(LoanRepository loanRepository,
                          PaymentRepository paymentRepository,
                          PaymentNotificationPublisher publisher) {
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
        this.publisher = publisher;
    }

    @Transactional
    @CacheEvict(value = "loansByUser", key = "#auth.name")
    public Payment recordPayment(CreatePaymentRequest req, Authentication auth) {
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be > 0");
        }

        Loan loan = loanRepository.findById(req.loanId())
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        // Simple ownership check: only owner can pay (admins can pay too due to role config)
        if (loan.getUser() != null && !loan.getUser().getUsername().equals(auth.getName())) {
            // If you want admins to pay on behalf of others, remove this check or gate it by role.
            throw new BadRequestException("You can only pay your own loans");
        }

        if (loan.getStatus() == LoanStatus.SETTLED) {
            throw new BadRequestException("Loan is already settled");
        }

        BigDecimal remaining = loan.getRemainingBalance();
        if (req.amount().compareTo(remaining) > 0) {
            throw new BadRequestException("Payment exceeds remaining balance");
        }

        BigDecimal newBalance = remaining.subtract(req.amount());
        loan.setRemainingBalance(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.SETTLED);
        }

        loanRepository.save(loan);

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .loan(loan)
                .amount(req.amount())
                .createdAt(Instant.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Publish notification best-effort (won't break payment if RabbitMQ isn't running)
        try {
            publisher.publishPaymentCreated(
                    "{\"loanId\":\"" + loan.getId() + "\",\"amount\":\"" + req.amount() + "\"}"
            );
        } catch (Exception ignored) { }

        return saved;
    }

    public List<Payment> listForLoan(UUID loanId) {
        return paymentRepository.findByLoanIdOrderByCreatedAtDesc(loanId);
    }
}