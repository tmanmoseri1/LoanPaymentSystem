package com.example.loanpayment.loan;

import com.example.loanpayment.loan.dto.CreateLoanRequest;
import com.example.loanpayment.loan.dto.LoanResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper mapper;

    public LoanController(LoanService loanService, LoanMapper mapper) {
        this.loanService = loanService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody CreateLoanRequest req, Authentication auth) {
        Loan loan = loanService.createLoan(req, auth);
        return ResponseEntity.ok(mapper.toResponse(loan));
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> get(@PathVariable UUID loanId) {
        return ResponseEntity.ok(mapper.toResponse(loanService.getLoan(loanId)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<LoanResponse>> myLoans(Authentication auth) {
        return ResponseEntity.ok(loanService.myLoans(auth).stream().map(mapper::toResponse).toList());
    }
}
