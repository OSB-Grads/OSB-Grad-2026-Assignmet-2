package com.bank.server.orchestrator;

import com.bank.server.dto.request.TransferRequestDTO;
import com.bank.server.dto.response.TransferResponse;
import com.bank.server.entity.Account;
import com.bank.server.entity.Transaction;
import com.bank.server.service.AccountService;
import com.bank.server.service.TransactionService;
import com.bank.server.utils.TransferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferOrchestrator {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransferValidator validator;

    @Transactional
    public TransferResponse transfer(
            String customerId,
            TransferRequestDTO request
    ) {
        validator.validateRequest(request);

        Account sourceAccount = accountService.getAccountForUpdate(
                        request.getSourceAccountNumber()
                );
        Account destinationAccount = accountService.getAccountForUpdate(
                        request.getDestinationAccountNumber()
                );
        validator.validateAccounts(
                customerId,
                request.getAmount(),
                sourceAccount,
                destinationAccount
        );
        accountService.transferAmount(
                sourceAccount,
                destinationAccount,
                request.getAmount()
        );
        Transaction transaction = transactionService.createTransferTransaction(
                        sourceAccount,
                        destinationAccount,
                        request.getAmount()
                );
        return transactionService.toTransferResponse(
                transaction,
                sourceAccount,
                destinationAccount
        );
    }
}