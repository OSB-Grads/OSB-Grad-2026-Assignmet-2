package com.bank.server.controller;

import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
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

    @PostMapping("/withdraw")
    public ResponseEntity<InboxResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO)
    {
        InboxResponseDTO withdrawDTO = inboxService.enqueueWithdrawal(withdrawRequestDTO);
        return new ResponseEntity<>(withdrawDTO,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InboxResponseDTO> getPaymentStatus(
            @PathVariable("id") String entryId) {

        InboxResponseDTO response = inboxService.getInboxStatus(entryId);

        return ResponseEntity.ok(response);
    }
}
