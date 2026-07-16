package com.bank.server.utils;

import com.bank.server.dto.request.TransferRequestDTO;
import com.bank.server.entity.Account;
import com.bank.server.exception.AccountOwnershipException;
import com.bank.server.exception.InsufficientBalanceException;
import com.bank.server.exception.InvalidTransferAmountException;
import com.bank.server.exception.SameAccountTransferException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransferValidator {
    public void validateRequest(TransferRequestDTO request) {

        TransferRequestDTO validRequest = Optional.ofNullable(request)
                        .orElseThrow(() -> new InvalidTransferAmountException(
                                        "INVALID_TRANSFER_REQUEST",
                                        "Transfer request cannot be null")
                        );

        Optional.ofNullable(validRequest.getSourceAccountNumber())
                .filter(accountNumber -> !accountNumber.isBlank())
                .orElseThrow(() -> new InvalidTransferAmountException(
                                "SOURCE_ACCOUNT_REQUIRED",
                                "Source account number is required")
                );
        Optional.ofNullable(validRequest.getDestinationAccountNumber())
                .filter(accountNumber -> !accountNumber.isBlank())
                .orElseThrow(() -> new InvalidTransferAmountException(
                                "DESTINATION_ACCOUNT_REQUIRED",
                                "Destination account number is required")
                );
        validateAmount(validRequest.getAmount());
    }
    public void validateAccounts(
            String customerId,
            BigDecimal amount,
            Account sourceAccount,
            Account destinationAccount
    ) {
        validateSameAccount(sourceAccount, destinationAccount);

        validateSourceAccountOwnership(customerId, sourceAccount);

        validateSufficientBalance(sourceAccount, amount);

        validateMinimumOperatingBalance(sourceAccount, amount);
    }
    private void validateAmount(BigDecimal amount) {
        Optional.ofNullable(amount).filter(value -> value.compareTo(BigDecimal.ZERO) > 0)
                .orElseThrow(() -> new InvalidTransferAmountException(
                                "INVALID_TRANSFER_AMOUNT",
                                "Transfer amount must be greater than zero")
                );
    }
    private void validateSameAccount(Account sourceAccount, Account destinationAccount) {
        boolean sameAccount = sourceAccount.getAccountNumber().equals(destinationAccount.getAccountNumber());

        if (sameAccount) {
            throw new SameAccountTransferException(
                    "SAME_ACCOUNT_TRANSFER",
                    "Source and destination accounts cannot be the same"
            );
        }
    }
    private void validateSourceAccountOwnership(String customerId, Account sourceAccount) {
        String sourceCustomerId = Optional.ofNullable(sourceAccount).map(Account::getCustomer)
                        .map(customer -> customer.getId())
                        .orElseThrow(() -> new AccountOwnershipException(
                                        "ACCOUNT_OWNERSHIP_MISMATCH",
                                        "Source account does not belong to the logged-in customer")
                        );
        Optional.ofNullable(customerId).filter(sourceCustomerId::equals)
                        .orElseThrow(() -> new AccountOwnershipException(
                                "ACCOUNT_OWNERSHIP_MISMATCH",
                                "Source account does not belong to the logged-in customer")
                );
    }
    private void validateSufficientBalance(Account sourceAccount, BigDecimal amount) {
        Optional.ofNullable(sourceAccount).map(Account::getBalance)
                .filter(balance -> balance.compareTo(amount) >= 0)
                .orElseThrow(() -> new InsufficientBalanceException(
                                "INSUFFICIENT_BALANCE",
                                "Insufficient balance in source account")
                );
    }
    private void validateMinimumOperatingBalance(Account sourceAccount, BigDecimal amount) {
        Optional<BigDecimal> minimumBalance = Optional.ofNullable(sourceAccount)
                        .map(Account::getProduct)
                        .map(product -> product.getMinOperatingBalance());

        if (minimumBalance.isEmpty()) {
            return;
        }

        BigDecimal remainingBalance = sourceAccount.getBalance().subtract(amount);

        minimumBalance.filter(minimum -> remainingBalance.compareTo(minimum) >= 0)
                .orElseThrow(() -> new InsufficientBalanceException(
                                "MINIMUM_BALANCE_VIOLATION",
                                "Transfer would violate the minimum operating balance")
                );
    }
}