package com.bank.server.service;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Loan;
import com.bank.server.enums.*;
import com.bank.server.exception.*;
import com.bank.server.mapper.LoanMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LoanRepository;
import com.bank.server.repository.ProductRepository;
import com.bank.server.utils.AuthenticationUtil;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.bank.server.entity.Product;
import com.bank.server.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;
    private final LoggerService loggerService;
    private final ProductRepository productRepository;

    public LoanDTO requestLoan(LoanRequestDTO request) {

        Authentication authentication = AuthenticationUtil.getAuthentication();
        String username = authentication.getName();

        Customer customer = customerRepository
                .findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException
                        ("CUSTOMER_FETCH", "Customer not found"));

        LoanCategory loanCategory = request.getLoanCategory();
        if (loanCategory == null) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Loan category is required");
        }

        Account account = accountRepository
                .findById(request.getDisbursementAccountId())
                .orElseThrow(() -> new AccountNotFoundException
                        ("ACCOUNT_FETCH", "Account not found"));

        // Ensuring the account belongs to the authenticated customer
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new LoanRequestException
                    ("LOAN_REQUEST", "Disbursement account does not belong to the authenticated customer");
        }
        if (request.getRequestedAmount() == null
                || request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Requested amount must be greater than zero"
            );
        }
        if (account.getIsLocked()) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Account is locked"
            );
        }
        // calculating the loan eligibility
        // formula - maxEligibilityAmount = acc.balance X getEligibilityMultiplier
        BigDecimal maxEligibleAmount = account.getBalance().multiply(loanCategory.getEligibilityMultiplier());

        Loan loan = loanMapper.toEntity(request);
        loan.setId(Generator.generateUuid());
        loan.setCustomer(customer);
        loan.setLoanCategory(loanCategory);
        loan.setDisbursementAccount(account);
        loan.setStatus(LoanStatus.PENDING);
        loan.setOfferedRate(loanCategory.getInterestRate());
        loan.setMaxEligibleAmount(maxEligibleAmount);
        Loan savedLoan = loanRepository.save(loan);

        loggerService.log(
                "LOAN_REQUEST",
                "Loan request created successfully with ID: " + savedLoan.getId(),
                LogType.SUCCESS
        );

        return loanMapper.toDto(savedLoan);
    }

    /*
     * Returns all loans belonging to the authenticated customer.
     */
    public List<LoanDTO> getCustomerLoans() {

        Authentication authentication = AuthenticationUtil.getAuthentication();
        String username = authentication.getName();

        Customer customer = customerRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "CUSTOMER_FETCH",
                                "Customer not found"
                        ));

        return loanRepository.findByCustomerId(customer.getId())
                .stream()
                .map(loanMapper::toDto)
                .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void processPendingLoans() {

        List<Loan> pendingLoans = loanRepository.findByStatus(LoanStatus.PENDING);

        Product loanProduct = productRepository
                .findByCategory(ProductCategory.LOAN_ACCOUNT)
                .orElseThrow(() -> new ProductNotFoundException(
                                "PRODUCT_FETCH",
                                "Loan product not found"
                        ));

        for (Loan loan : pendingLoans) {

            if (loan.getRequestedAmount().compareTo(loan.getMaxEligibleAmount()) > 0) {
                loan.setStatus(LoanStatus.REJECTED);
                loggerService.log(
                        "LOAN_REJECT",
                        "Loan rejected for customer "
                                + loan.getCustomer().getId(),
                        LogType.FAILURE
                );
                loanRepository.save(loan);
                continue;
            }
            if (pendingLoans.isEmpty()) {
                loggerService.log(
                        "LOAN_PROCESS",
                        "No pending loans found",
                        LogType.SUCCESS
                );
                return;
            }

            Account account = new Account();
            account.setId(Generator.generateUuid());
            account.setAccountNumber(Generator.generateAccountNumber());
            account.setCustomer(loan.getCustomer());
            account.setProduct(loanProduct);
            account.setBalance(loan.getRequestedAmount());
            account.setIsLocked(false);
            account.setTransfersEnabled(false);
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
            loan.setStatus(LoanStatus.APPROVED);
            loanRepository.save(loan);

            loggerService.log(
                    "LOAN_APPROVE",
                    "Loan approved with ID: "
                            + loan.getId(),
                    LogType.SUCCESS
            );
        }
    }
}