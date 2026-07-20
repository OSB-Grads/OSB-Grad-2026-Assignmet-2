package com.bank.server.service;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.dto.response.LoanResponseDTO;
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
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.bank.server.entity.Product;

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

    public LoanResponseDTO requestLoan(LoanRequestDTO request) {
        String customerId = AuthenticationUtil
                .getCurrentUser()
                .getCustomerId();

        // Find the customer record corresponding to the logged-in user
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException
                        ("CUSTOMER_NOT_FOUND", "Customer not found"));

        // Loan category is mandatory for every loan request
        LoanCategory loanCategory = request.getLoanCategory();
        if (loanCategory == null) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Loan category is required");
        }


        // checking if the requested amount is greater than zero
        if (request.getRequestedAmount() == null || request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanRequestException(
                    "LOAN_REQUEST",
                    "Requested amount must be greater than zero"
            );
        }
        // Convert request DTO into Loan entity
        Loan loan = loanMapper.toEntity(request);

        // Populate fields that should never come directly from the client
        loan.setStatus(LoanStatus.PENDING);
        loan.setId(Generator.generateUuid());
        loan.setCustomer(customer);
        loan.setLoanCategory(loanCategory);
        loan.setOfferedRate(loanCategory.getInterestRate());
        loanRepository.save(loan);

        // Record successful loan request for auditing
        loggerService.log(
                "LOAN_REQUEST",
                "Loan request created successfully with ID: " + loan.getId(),
                LogType.SUCCESS
        );

        return new LoanResponseDTO(true);

    }

    /*
     * Returns all loans belonging to the authenticated customer.
     */
    public List<LoanDTO> getCustomerLoans() {
        String customerId = AuthenticationUtil
                .getCurrentUser()
                .getCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUSTOMER_NOT_FOUND",
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
        // Fetch all loan requests waiting for admin approval
        List<Loan> pendingLoans = loanRepository.findByStatus(LoanStatus.PENDING);
        // If there are no pending requests, stop processing
        if (pendingLoans.isEmpty()) {
            loggerService.log(
                    "LOAN_PROCESS",
                    "No pending loans found",
                    LogType.SUCCESS
            );
            return;
        }
        // Admin processes each pending loan one by one
        for (Loan loan : pendingLoans) {

            // Fetch the loan product associated with the selected loan category
            // Retrieve the loan product corresponding to the loan category
           // PERSONAL -> Personal Loan Product
            List<Product> products = productRepository.findByLoanCategory(loan.getLoanCategory());

            // Every loan category should have one configured loan product
            if (products.isEmpty()) {
                throw new ProductNotFoundException(
                        "PRODUCT_NOT_FOUND",
                        "No product found for loan category: " + loan.getLoanCategory()
                );
            }

            // use the first product from the list
            Product loanProduct = products.get(0);

            // Create a new loan account for the customer
            // The requested loan amount is credited as the opening balance
            Account account = Account.builder()
                    .id(Generator.generateUuid())
                    .accountNumber(Generator.generateAccountNumber())
                    .customer(loan.getCustomer())
                    .product(loanProduct)
                    .balance(loan.getRequestedAmount())
                    .isLocked(false)
                    .transfersEnabled(false)
                    .status(AccountStatus.ACTIVE)
                    .build();

            // Save the newly created loan account
            Account savedAccount = accountRepository.save(account);

            // Associate the created account with the approved loan
            loan.setAccount(savedAccount);

            // Update the loan status from PENDING to APPROVED
            loan.setStatus(LoanStatus.APPROVED);
            loanRepository.save(loan);

            loggerService.log(
                    "LOAN_APPROVE",
                    "Loan approved successfully. Loan ID: " + loan.getId(),
                    LogType.SUCCESS);



        }
    }
}