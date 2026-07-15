package com.bank.server.utils;

import com.bank.server.dto.request.TransferRequestDTO;
import com.bank.server.entity.Account;
import com.bank.server.enums.AccountStatus;
import com.bank.server.exception.AccountOwnershipException;
import com.bank.server.exception.InsufficientBalanceException;
import com.bank.server.exception.InvalidTransferAmountException;
import com.bank.server.exception.SameAccountTransferException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferValidator {
    public void validateRequest(TransferRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Transfer request cannot be null"
            );
        }
        if (request.getSourceAccountNumber() == null
                || request.getSourceAccountNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Source account number is required"
            );
        }
        if (request.getDestinationAccountNumber() == null
                || request.getDestinationAccountNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Destination account number is required"
            );
        }
        validateAmount(request.getAmount());
    }
    public void validateAccounts(
            String customerId,
            BigDecimal amount,
            Account sourceAccount,
            Account destinationAccount
    ) {

        validateSameAccount(sourceAccount, destinationAccount);

        validateSourceAccountOwnership(customerId, sourceAccount);

        validateAccountAvailability(sourceAccount, "Source");
        validateAccountAvailability(destinationAccount, "Destination");

        validateSufficientBalance(sourceAccount, amount);

        validateMinimumBalance(sourceAccount, amount);
    }
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidTransferAmountException(
                    "Transfer amount cannot be null"
            );
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferAmountException(
                    "Transfer amount must be greater than zero"
            );
        }
    }
    private void validateSameAccount(Account sourceAccount, Account destinationAccount) {
        if (sourceAccount.getAccountNumber().equals(destinationAccount.getAccountNumber())) {
            throw new SameAccountTransferException(
                    "Source and destination accounts cannot be the same"
            );
        }
    }
    private void validateSourceAccountOwnership(
            String customerId,
            Account sourceAccount
    ) {
        if (customerId == null || sourceAccount.getCustomer() == null
                || sourceAccount.getCustomer().getId() == null || !sourceAccount.getCustomer()
                .getId()
                .equals(customerId)) {
            throw new AccountOwnershipException(
                    "Source account does not belong to the logged-in customer"
            );
        }
    }
    private void validateAccountAvailability(
            Account account,
            String accountType
    ) {
        if (account.isLocked()) {
            throw new IllegalStateException(
                    accountType + " account is locked"
            );
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    accountType + " account is not active"
            );
        }
    }
    private void validateSufficientBalance(
            Account sourceAccount,
            BigDecimal amount
    ) {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in source account"
            );
        }
    }
    private void validateMinimumBalance(
            Account sourceAccount,
            BigDecimal amount
    ) {
        if (sourceAccount.getProduct() == null || sourceAccount.getProduct()
                .getMinOperatingBalance() == null) {
            return;
        }
        BigDecimal remainingBalance = sourceAccount.getBalance().subtract(amount);

        BigDecimal minimumBalance = sourceAccount.getProduct().getMinOperatingBalance();

        if (remainingBalance.compareTo(minimumBalance) < 0) {
            throw new InsufficientBalanceException(
                    "Transfer would violate the minimum operating balance"
            );
        }
    }
}