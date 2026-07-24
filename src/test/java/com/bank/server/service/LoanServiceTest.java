package com.bank.server.service;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Loan;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.enums.LoanCategory;
import com.bank.server.enums.LoanStatus;
import com.bank.server.enums.LogType;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.LoanRequestException;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.LoanMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LoanRepository;
import com.bank.server.repository.ProductRepository;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.utils.AuthenticationUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    private static final String CUSTOMER_ID = "cust1";
    private static final BigDecimal LOAN_AMOUNT = BigDecimal.valueOf(10000);


    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private LoggerService loggerService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private LoanService loanService;


    // creates a fake customer used in tests
    private Customer createCustomer() {

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        return customer;
    }


    // creates a valid loan request used in tests
    private LoanRequestDTO createLoanRequest() {

        LoanRequestDTO request = new LoanRequestDTO();
        request.setLoanCategory(LoanCategory.PERSONAL);
        request.setRequestedAmount(LOAN_AMOUNT);

        return request;
    }
    // creates a fake pending loan used in tests
    private Loan createPendingLoan(Customer customer) {
        Loan loan = new Loan();
        loan.setId("loan1");
        loan.setCustomer(customer);
        loan.setLoanCategory(LoanCategory.PERSONAL);
        loan.setRequestedAmount(LOAN_AMOUNT);
        loan.setStatus(LoanStatus.PENDING);
        return loan;
    }


    @Nested
    class RequestLoanTests {

        @Test
        void shouldCreateLoanRequest() {
            // Arrange
            // Create the loan request
            LoanRequestDTO request = createLoanRequest();

            // Create a fake customer returned by the repository
            Customer customer = createCustomer();

            // Mock the static AuthenticationUtil class
            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                // Create a Loan object that the mapper should return
                Loan loan = new Loan();
                loan.setRequestedAmount(request.getRequestedAmount());
                loan.setLoanCategory(request.getLoanCategory());

                // Mock the mapper to convert LoanRequestDTO into a Loan entity
                when(loanMapper.toEntity(request))
                        .thenReturn(loan);

                // Return the mocked logged-in user whenever AuthenticationUtil.getCurrentUser() is called
                auth.when(AuthenticationUtil::getCurrentUser)
                        .thenReturn(customUserDetails);

                // Mock the logged-in customer's ID
                when(customUserDetails.getCustomerId())
                        .thenReturn(CUSTOMER_ID);

                // Mock fetching the customer from the database
                when(customerRepository.findById(CUSTOMER_ID))
                        .thenReturn(Optional.of(customer));

                // Actual method call
                loanService.requestLoan(request);

                // Assert

                // creating a box to store the loan
                ArgumentCaptor<Loan> loanCaptor =
                        ArgumentCaptor.forClass(Loan.class);

                // here we are verifying that save() is called, and loan is put into the loan captor
                verify(loanRepository).save(loanCaptor.capture());

                // verify mapper is called
                verify(loanMapper).toEntity(request);

                // verify customer lookup happened
                verify(customerRepository).findById(CUSTOMER_ID);

                // gets the captured loan object
                Loan savedLoan = loanCaptor.getValue();

                // Verify that the customer was set correctly
                assertEquals(customer, savedLoan.getCustomer());

                // Verify loan category
                assertEquals(
                        LoanCategory.PERSONAL,
                        savedLoan.getLoanCategory()
                );

                // Verify requested amount
                assertEquals(
                        LOAN_AMOUNT,
                        savedLoan.getRequestedAmount()
                );

                // Verify loan status
                assertEquals(
                        LoanStatus.PENDING,
                        savedLoan.getStatus()
                );

                // Verify offered rate
                assertEquals(LoanCategory.PERSONAL.getInterestRate(), savedLoan.getOfferedRate());

                // Verify loan id was generated
                assertNotNull(savedLoan.getId());
            }
        }


        @Test
        void shouldThrowExceptionWhenCustomerNotFound() {

            // create the loan request
            LoanRequestDTO request = createLoanRequest();

            // mock the static AuthenticationUtil class
            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                // return the mocked logged-in user
                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                // mock the logged-in customer id
                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                // customer does not exist in the repository
                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

                // verify that CustomerNotFoundException is thrown
                assertThrows(CustomerNotFoundException.class, () -> loanService.requestLoan(request));

                // verify customer repository was called
                verify(customerRepository).findById(CUSTOMER_ID);

                // verify loan was never saved
                verify(loanRepository, never()).save(any(Loan.class));
            }
        }


        @Test
        void shouldThrowExceptionWhenLoanCategoryIsNull() {

            // create the loan request
            LoanRequestDTO request = createLoanRequest();
            request.setLoanCategory(null);

            // Create a fake customer returned by the repository
            Customer customer = createCustomer();

            // mock the static AuthenticationUtil class
            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                // return the mocked logged-in user
                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                // mock the logged-in customer id
                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                // Mock fetching the customer from the database
                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

                // verify that LoanRequestException is thrown
                assertThrows(
                        LoanRequestException.class,
                        () -> loanService.requestLoan(request)
                );

                // verify mapper was never called
                verify(loanMapper, never()).toEntity(any(LoanRequestDTO.class));

                // verify loan was never saved
                verify(loanRepository, never()).save(any(Loan.class));
            }
        }


        @Test
        void shouldThrowExceptionWhenRequestedAmountIsNull() {

            // create the loan request
            LoanRequestDTO request = createLoanRequest();
            request.setRequestedAmount(null);

            Customer customer = createCustomer();

            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

                // verify that LoanRequestException is thrown
                assertThrows(LoanRequestException.class, () -> loanService.requestLoan(request));

                // verify mapper was never called
                verify(loanMapper, never()).toEntity(any(LoanRequestDTO.class));

                // verify loan was never saved
                verify(loanRepository, never()).save(any(Loan.class));
            }
        }


        @Test
        void shouldThrowExceptionWhenRequestedAmountIsZero() {

            // create the loan request
            LoanRequestDTO request = createLoanRequest();
            request.setRequestedAmount(BigDecimal.ZERO);

            Customer customer = createCustomer();

            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

                // verify that LoanRequestException is thrown
                assertThrows(LoanRequestException.class, () -> loanService.requestLoan(request)
                );

                // verify mapper was never called
                verify(loanMapper, never()).toEntity(any(LoanRequestDTO.class));

                // verify loan was never saved
                verify(loanRepository, never()).save(any(Loan.class));
            }
        }


        @Test
        void shouldThrowExceptionWhenRequestedAmountIsNegative() {

            // create the loan request
            LoanRequestDTO request = createLoanRequest();
            request.setRequestedAmount(BigDecimal.valueOf(-1000));

            Customer customer = createCustomer();

            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

                // verify that LoanRequestException is thrown
                assertThrows(LoanRequestException.class, () -> loanService.requestLoan(request));

                // verify mapper was never called
                verify(loanMapper, never()).toEntity(any(LoanRequestDTO.class));

                // verify loan was never saved
                verify(loanRepository, never()).save(any(Loan.class));
            }
        }
    }


    @Nested
    class GetCustomerLoansTests {


        @Test
        void shouldReturnCustomerLoans() {

            // create a fake customer returned by the repository
            Customer customer = createCustomer();

            // create fake loan
            Loan loan = createPendingLoan(customer);

            // create fake loan dto
            LoanDTO loanDTO = new LoanDTO();

            // mock the static AuthenticationUtil class
            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                // return the mocked logged-in user
                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                // mock the logged-in customer id
                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                // Mock fetching the customer from the database
                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

                // mock fetching loans belonging to the customer
                when(loanRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(List.of(loan));

                // mock converting loan entity into loan dto
                when(loanMapper.toDto(loan)).thenReturn(loanDTO);

                // Actual method call
                List<LoanDTO> result = loanService.getCustomerLoans();

                // Assert

                // verify customer repository was called
                verify(customerRepository).findById(CUSTOMER_ID);

                // verify customer's loans were fetched
                verify(loanRepository).findByCustomerId(CUSTOMER_ID);

                // verify loan was converted into dto
                verify(loanMapper).toDto(loan);

                // verify one loan is returned
                assertEquals(1, result.size());

                // verify returned loan dto is correct
                assertEquals(loanDTO, result.get(0));
            }
        }


        @Test
        void shouldThrowExceptionWhenCustomerNotFoundForGetCustomerLoans() {

            // mock the static AuthenticationUtil class
            try (MockedStatic<AuthenticationUtil> auth =
                         Mockito.mockStatic(AuthenticationUtil.class)) {

                auth.when(AuthenticationUtil::getCurrentUser).thenReturn(customUserDetails);

                when(customUserDetails.getCustomerId()).thenReturn(CUSTOMER_ID);

                when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

                assertThrows(CustomerNotFoundException.class, () -> loanService.getCustomerLoans());

                verify(customerRepository).findById(CUSTOMER_ID);
                verify(loanRepository, never()).findByCustomerId(any());
                verify(loanMapper, never()).toDto(any(Loan.class));
            }
        }
    }


    @Nested
    class GetPendingLoansTests {


        @Test
        void shouldReturnPendingLoans() {

            // create a fake pending loan
            Loan loan = createPendingLoan(createCustomer());

            // create fake loan dto
            LoanDTO loanDTO = new LoanDTO();

            // mock fetching pending loans from the repository
            when(loanRepository.findByStatus(LoanStatus.PENDING)).thenReturn(List.of(loan));

            // mock converting loan entity into loan dto
            when(loanMapper.toDto(loan)).thenReturn(loanDTO);

            // Actual method call
            List<LoanDTO> result = loanService.getPendingLoans();

            // Assert

            verify(loanRepository)
                    .findByStatus(LoanStatus.PENDING);

            verify(loanMapper)
                    .toDto(loan);

            assertEquals(1, result.size());
            assertEquals(loanDTO, result.get(0));
        }


        @Test
        void shouldReturnEmptyListWhenNoPendingLoans() {

            // mock repository to return no pending loans
            when(loanRepository.findByStatus(LoanStatus.PENDING)).thenReturn(List.of());

            // Actual method call
            List<LoanDTO> result = loanService.getPendingLoans();

            // Assert

            verify(loanRepository).findByStatus(LoanStatus.PENDING);
            verify(loanMapper, never()).toDto(any(Loan.class));

            verify(loggerService).log(
                    "LOAN_FETCH",
                    "No pending loans found",
                    LogType.SUCCESS
            );

            assertEquals(0, result.size());
        }
    }


    @Nested
    class ProcessPendingLoansTests {


        @Test
        void shouldProcessPendingLoans() {

            // create a fake customer
            Customer customer = createCustomer();

            // create a fake pending loan
            Loan loan = createPendingLoan(customer);

            // create a fake loan product
            Product product = new Product();

            // create the account which will be returned after saving
            Account savedAccount = new Account();
            savedAccount.setId("account1");

            // mock fetching pending loans
            when(loanRepository.findByStatus(LoanStatus.PENDING))
                    .thenReturn(List.of(loan));

            // mock fetching product for the loan category
            when(productRepository.findByLoanCategory(LoanCategory.PERSONAL)).thenReturn(List.of(product));

            // mock saving the newly created account
            when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

            // Actual method call
            loanService.processPendingLoans();

            // Assert
            verify(loanRepository).findByStatus(LoanStatus.PENDING);
            verify(productRepository).findByLoanCategory(LoanCategory.PERSONAL);

            // creating a box to store the account
            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

            // verify account was saved and capture the account
            verify(accountRepository)
                    .save(accountCaptor.capture());

            // gets the captured account objects
            Account createdAccount = accountCaptor.getValue();

            assertEquals(customer, createdAccount.getCustomer());

            assertEquals(product, createdAccount.getProduct());

            assertEquals(
                    LOAN_AMOUNT,
                    createdAccount.getBalance()
            );

            assertEquals(
                    AccountStatus.ACTIVE,
                    createdAccount.getStatus()
            );

            // verify transfers are disabled for loan account
            assertFalse(createdAccount.getTransfersEnabled());

            // verify account is not locked
            assertFalse(createdAccount.getLocked());

            // verify account id was generated
            assertNotNull(createdAccount.getId());

            // verify account number was generated
            assertNotNull(createdAccount.getAccountNumber());

            // verify saved account was assigned to the loan
            assertEquals(
                    savedAccount,
                    loan.getAccount()
            );

            // verify loan status changed from PENDING to APPROVED
            assertEquals(
                    LoanStatus.APPROVED,
                    loan.getStatus()
            );

            // verify updated loan was saved
            verify(loanRepository).save(loan);
        }


        @Test
        void shouldReturnWhenNoPendingLoans() {

            // mock repository to return no pending loans
            when(loanRepository.findByStatus(LoanStatus.PENDING)).thenReturn(List.of());
            // Actual method call
            loanService.processPendingLoans();

            // Assert

            verify(loanRepository).findByStatus(LoanStatus.PENDING);
            verify(loggerService).log(
                    "LOAN_PROCESS",
                    "No pending loans found",
                    LogType.SUCCESS
            );

            verify(productRepository, never()).findByLoanCategory(any(LoanCategory.class));
            verify(accountRepository, never()).save(any(Account.class));
            verify(loanRepository, never()).save(any(Loan.class));
        }


        @Test
        void shouldThrowExceptionWhenProductNotFound() {

            // create a fake customer
            Customer customer = createCustomer();

            // create a fake pending loan
            Loan loan = createPendingLoan(customer);

            // mock fetching pending loans
            when(loanRepository.findByStatus(LoanStatus.PENDING)).thenReturn(List.of(loan));

            // mock product repository to return no product
            when(productRepository.findByLoanCategory(LoanCategory.PERSONAL)).thenReturn(List.of());

            // verify that ProductNotFoundException is thrown
            assertThrows(ProductNotFoundException.class, () -> loanService.processPendingLoans());

            verify(loanRepository).findByStatus(LoanStatus.PENDING);
            verify(productRepository).findByLoanCategory(LoanCategory.PERSONAL);
            verify(accountRepository, never()).save(any(Account.class));
            verify(loanRepository, never()).save(any(Loan.class));
        }
    }
}