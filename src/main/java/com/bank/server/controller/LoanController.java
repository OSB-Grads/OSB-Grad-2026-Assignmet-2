package com.bank.server.controller;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.response.LoanResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;


    // customer requests the loan
    @PostMapping("/requests")
    public LoanResponseDTO requestLoan(@Valid @RequestBody LoanRequestDTO request) {
        return loanService.requestLoan(request);
    }

    // customer views their loans
    @GetMapping
    public List<LoanDTO> getCustomerLoans() {
        return loanService.getCustomerLoans();
    }

    // admin only can process the loans
    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    public void processPendingLoans() {
        loanService.processPendingLoans();
    }
    // admin can see the pending loans
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanDTO> getPendingLoans() {
        return loanService.getPendingLoans();
    }
}