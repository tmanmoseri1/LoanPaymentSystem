package com.example.loanpayment.payment;

import com.example.loanpayment.payment.dto.CreatePaymentRequest;
import com.example.loanpayment.payment.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper mapper;

    public PaymentController(PaymentService paymentService, PaymentMapper mapper) {
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest req, Authentication auth) {
        return ResponseEntity.ok(mapper.toResponse(paymentService.recordPayment(req, auth)));
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentResponse>> list(@PathVariable UUID loanId) {
        return ResponseEntity.ok(paymentService.listForLoan(loanId).stream().map(mapper::toResponse).toList());
    }
}
