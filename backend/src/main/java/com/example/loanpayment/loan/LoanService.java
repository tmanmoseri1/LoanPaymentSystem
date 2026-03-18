package com.example.loanpayment.loan;

import com.example.loanpayment.common.BadRequestException;
import com.example.loanpayment.common.NotFoundException;
import com.example.loanpayment.loan.dto.CreateLoanRequest;
import com.example.loanpayment.security.User;
import com.example.loanpayment.security.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = "loansByUser", key = "#auth.name")
    public Loan createLoan(CreateLoanRequest req, Authentication auth) {
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Loan amount must be > 0");
        }
        if (req.termMonths() <= 0) {
            throw new BadRequestException("Term must be > 0");
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .user(user)
                .originalAmount(req.amount())
                .termMonths(req.termMonths())
                .remainingBalance(req.amount())
                .status(LoanStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        return loanRepository.save(loan);
    }

    public Loan getLoan(UUID id) {
        return loanRepository.findById(id).orElseThrow(() -> new NotFoundException("Loan not found"));
    }

    @Cacheable(value = "loansByUser", key = "#auth.name")
    public List<Loan> myLoans(Authentication auth) {
        return loanRepository.findByUserUsernameOrderByCreatedAtDesc(auth.getName());
    }
}
