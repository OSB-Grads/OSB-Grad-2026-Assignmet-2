package com.bank.server.controller;

import com.bank.server.dto.request.DepositRequestDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.PaymentResponseDTO;
import com.bank.server.entity.PaymentQueue;
import com.bank.server.service.PaymentQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentQueueService paymentQueueService;
    @PostMapping("/deposit")
    public ResponseEntity<PaymentResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO depositRequestDTO)
    {
        PaymentResponseDTO depositDTO = paymentQueueService.enqueueDeposit(depositRequestDTO);
        return new ResponseEntity<>(depositDTO,HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<PaymentResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO)
    {
        PaymentResponseDTO withdrawDTO = paymentQueueService.enqueueWithdrawal(withdrawRequestDTO);
        return new ResponseEntity<>(withdrawDTO,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(
            @PathVariable("id") String entryId) {

        PaymentResponseDTO response = paymentQueueService.getPaymentStatus(entryId);

        return ResponseEntity.ok(response);
    }
}
