package com.bank.server.controller;

import com.bank.server.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process/deposits")
    public ResponseEntity<String> runDeposits(){
        paymentService.processDeposits();
        return ResponseEntity.ok("Deposit process completed successfully");
    }

    @PostMapping("/process/withdrawals")
    public ResponseEntity<String> runWithdrawals(){
        paymentService.processWithdrawals();
        return ResponseEntity.ok("Withdrawal process completed successfully");
    }
}
