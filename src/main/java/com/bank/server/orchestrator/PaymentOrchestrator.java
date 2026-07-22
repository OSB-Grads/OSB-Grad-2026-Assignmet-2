package com.bank.server.orchestrator;

import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentOrchestrator {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final InboxService inboxService;
    private final CustomerService customerService;

    public InboxResponseDTO processWithdrawalRequest(WithdrawRequestDTO withdrawRequestDTO)
    {
        BigDecimal amount = withdrawRequestDTO.getAmount();

        AccountDTO accountDTO = accountService.reserveAmount(withdrawRequestDTO.getTargetAccountId(), amount);

        CustomerDTO customer = customerService.getCustomerById(accountDTO.getCustomerId());

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .customerId(accountDTO.getCustomerId())
                .fromAccountId(withdrawRequestDTO.getTargetAccountId())
                .transactionType("WITHDRAWAL")
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .description("Withdrawal Initiated")
                .build();

        TransactionDTO transaction =  transactionService.createTransaction(transactionDTO);

        InboxResponseDTO inboxResponse = inboxService.enqueueWithdrawal(withdrawRequestDTO,transaction.getId(),customer.getNationalId());

        return inboxResponse;
    }
}
