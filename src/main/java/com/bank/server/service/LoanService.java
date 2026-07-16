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
    // gets the currently logged-in user
        Authentication authentication = AuthenticationUtil.getAuthentication();
        String username = authentication.getName();
    // fetch the authenticated customer's record
        Customer customer = customerRepository
                .findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException
                        ("CUSTOMER_FETCH", "Customer not found"));
    // prevents the user from submitting the loan request without relevant category
        LoanCategory loanCategory = request.getLoanCategory();
        if (loanCategory == null) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Loan category is required");
        }


        // checking if the account exists or not
        Account account = accountRepository
                .findById(request.getDisbursementAccountId())
                .orElseThrow(() -> new AccountNotFoundException
                        ("ACCOUNT_FETCH", "Account not found"));

        // Ensuring the account belongs to the authenticated customer
        // Without this, a customer could potentially request a loan using another customer's account

        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new LoanRequestException
                    ("LOAN_REQUEST", "Disbursement account does not belong to the authenticated customer");
        }

        // checking if the requested amount is greater than zero
        if (request.getRequestedAmount() == null || request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Requested amount must be greater than zero"
            );
        }
        // locked account check
        if (account.getIsLocked()) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Account is locked"
            );
        }
        // calculating the loan eligibility
        // formula - maxEligibilityAmount = acc.balance X getEligibilityMultiplier
        BigDecimal maxEligibleAmount = account.getBalance().multiply(loanCategory.getEligibilityMultiplier());

        // here mapper is converting our loan Dto to loan entity
        Loan loan = loanMapper.toEntity(request);
        loan.setStatus(LoanStatus.PENDING);
        loan.setId(Generator.generateUuid());
        loan.setCustomer(customer);
        loan.setLoanCategory(loanCategory);
        loan.setDisbursementAccount(account);
        loan.setOfferedRate(loanCategory.getInterestRate());
        loan.setMaxEligibleAmount(maxEligibleAmount);
        Loan savedLoan = loanRepository.save(loan);  // saves to db

        loggerService.log(
                "LOAN_REQUEST",
                "Loan request created successfully with ID: " + savedLoan.getId(),
                LogType.SUCCESS
        );

        return loanMapper.toDto(savedLoan);
        // converts the saved record back to dto.
    }

    /*
     * Returns all loans belonging to the authenticated customer.
     */
    public List<LoanDTO> getCustomerLoans() {
        // again gets the current logged-in user
        Authentication authentication = AuthenticationUtil.getAuthentication();
        String username = authentication.getName();
        // finding the customer record
        Customer customer = customerRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "CUSTOMER_FETCH",
                                "Customer not found"
                        ));
        // returns the loan record in list, but before returning converting them back to dto
        return loanRepository.findByCustomerId(customer.getId())
                .stream()
                .map(loanMapper::toDto)
                .toList();
    }
    // admin has to see the pending loans
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanDTO> getPendingLoans() {
        // fetching all loans with PENDING status
        List<LoanDTO> pendingLoans = loanRepository.findByStatus(LoanStatus.PENDING)
                .stream()
                .map(loanMapper::toDto)
                .toList();

        if (pendingLoans.isEmpty()) {
            loggerService.log(
                    "LOAN_FETCH",
                    "No pending loans found",
                    LogType.SUCCESS
            );
            return pendingLoans;
        }
        // just an edge case : if loans aren't there
        loggerService.log(
                "LOAN_FETCH",
                "Fetched pending loans successfully",
                LogType.SUCCESS
        );
        // return the pending loans list
        return pendingLoans;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void processPendingLoans() {
        // fetching the loans and check if the list is empty.
        List<Loan> pendingLoans = loanRepository.findByStatus(LoanStatus.PENDING);
        if (pendingLoans.isEmpty()) {
            loggerService.log(
                    "LOAN_PROCESS",
                    "No pending loans found",
                    LogType.SUCCESS
            );
            return;
        }


        // getting the product category which is loan_account or else throw exception.
        // ( FETCH LOAN_ACCOUNT product )
        Product loanProduct = productRepository
                .findByCategory(ProductCategory.LOAN_ACCOUNT)
                .orElseThrow(() -> new ProductNotFoundException(
                                "PRODUCT_FETCH",
                                "Loan product not found"));
        // traverse through each of pending loans
        for (Loan loan : pendingLoans) {
            // check the loan conditions, if everything is fine, then move forward. or reject (log rejection)
            // Reject loan if requested amount exceeds the maximum eligible amount
                if (loan.getRequestedAmount().compareTo(loan.getMaxEligibleAmount()) > 0) {
                loan.setStatus(LoanStatus.REJECTED);
                loggerService.log(
                        "LOAN_REJECT",
                        "Loan rejected for customer "
                                + loan.getCustomer().getId(),
                        LogType.FAILURE
                );
                // save the loan
                loanRepository.save(loan);
                continue;
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