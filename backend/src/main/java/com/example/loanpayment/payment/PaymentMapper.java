package com.example.loanpayment.payment;

import com.example.loanpayment.payment.dto.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "loanId", source = "loan.id")
    PaymentResponse toResponse(Payment payment);
}
