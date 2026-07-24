package com.bank.server.controller;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.dto.response.LoanResponseDTO;
import com.bank.server.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;


    // customer requests the loan
    @PostMapping("/requests")
    public LoanResponseDTO requestLoan(
            @Valid @RequestBody LoanRequestDTO request) {

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
    public ResponseEntity<Map<String, String>> processPendingLoans() {

        String message = loanService.processPendingLoans();

        return ResponseEntity.ok(
                Map.of("message", message)
        );
    }


    // admin can see the pending loans
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingLoans() {

        List<LoanDTO> pendingLoans = loanService.getPendingLoans();

        if (pendingLoans.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "No pending loans found")
            );
        }

        return ResponseEntity.ok(pendingLoans);
    }
}