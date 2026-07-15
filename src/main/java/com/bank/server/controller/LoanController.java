package com.bank.server.controller;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/requests")
    public LoanDTO requestLoan(@Valid @RequestBody LoanRequestDTO request) {
        return loanService.requestLoan(request);
    }

    @GetMapping
    public List<LoanDTO> getCustomerLoans() {
        return loanService.getCustomerLoans();
    }
    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    public void processPendingLoans() {
        loanService.processPendingLoans();
    }
}