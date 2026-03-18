package com.example.loanpayment.loan;

import com.example.loanpayment.loan.dto.LoanResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanResponse toResponse(Loan loan);
}
