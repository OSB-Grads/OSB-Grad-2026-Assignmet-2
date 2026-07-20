package com.bank.server.controller;

import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.dto.response.PaymentResponseDTO;
import com.bank.server.orchestrator.PaymentOrchestrator;
import com.bank.server.service.InboxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final InboxService inboxService;
    private final PaymentOrchestrator paymentOrchestrator;

    @PostMapping("/withdraw")
    public ResponseEntity<InboxResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO)
    {
        InboxResponseDTO withdrawDTO = paymentOrchestrator.processWithdrawalRequest(withdrawRequestDTO);
        return new ResponseEntity<>(withdrawDTO,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(
            @PathVariable("id") String entryId) {

        PaymentResponseDTO response = inboxService.getInboxStatus(entryId);

        return ResponseEntity.ok(response);
    }
}
