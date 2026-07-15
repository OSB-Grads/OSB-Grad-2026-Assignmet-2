package com.bank.server.service;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Loan;
import com.bank.server.entity.LoanCategory;
import com.bank.server.enums.LoanStatus;
import com.bank.server.enums.LogType;
import com.bank.server.exception.*;
import com.bank.server.mapper.LoanMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LoanCategoryRepository;
import com.bank.server.repository.LoanRepository;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanCategoryRepository loanCategoryRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;
    private final LoggerService loggerService;

    public LoanDTO requestLoan(LoanRequestDTO request) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = authentication.getName();

        Customer customer = customerRepository
                .findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException
                        ("CUSTOMER_FETCH", "Customer not found"));

        LoanCategory loanCategory = loanCategoryRepository
                .findById(request.getLoanCategoryId())
                .orElseThrow(() -> new LoanCategoryNotFoundException
                        ("LOAN_CATEGORY_FETCH", "Loan category not found"));

        Account account = accountRepository
                .findById(request.getDisbursementAccountId())
                .orElseThrow(() -> new AccountNotFoundException
                        ("ACCOUNT_FETCH", "Account not found"));

        // Ensuring the account belongs to the authenticated customer
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new LoanRequestException
                    ("LOAN_REQUEST", "Disbursement account does not belong to the authenticated customer");
        }
        // calculating the loan eligibility
        // formula - maxEligibilityAmount = acc.balance X getEligibilityMultiplier
        BigDecimal maxEligibleAmount = account.getBalance().multiply(loanCategory.getEligibilityMultiplier());

        // validate requested amount
        if (request.getRequestedAmount().compareTo(maxEligibleAmount) > 0) {
            throw new LoanEligibilityExceededException
                    ("LOAN_REQUEST", "Requested amount exceeds maximum eligible amount");
        }

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

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

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
}