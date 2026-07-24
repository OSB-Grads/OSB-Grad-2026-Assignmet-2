package com.bank.server.config;

import com.bank.server.entity.Product;
import com.bank.server.enums.ProductCategory;
import com.bank.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.bank.server.enums.LoanCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@Order(1)
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {

        if (productRepository.count() == 0) {

            Product savingsBasic = new Product();
            savingsBasic.setId(UUID.randomUUID().toString());
            savingsBasic.setProductName("Savings Basic");
            savingsBasic.setCategory(ProductCategory.SAVINGS);
            savingsBasic.setInterestRate(new BigDecimal("4.50"));
            savingsBasic.setMinOperatingBalance(new BigDecimal("1000.00"));

            Product savingsPremium = new Product();
            savingsPremium.setId(UUID.randomUUID().toString());
            savingsPremium.setProductName("Savings Premium");
            savingsPremium.setCategory(ProductCategory.SAVINGS);
            savingsPremium.setInterestRate(new BigDecimal("5.00"));
            savingsPremium.setMinOperatingBalance(new BigDecimal("5000.00"));

            Product fixedDeposit12 = new Product();
            fixedDeposit12.setId(UUID.randomUUID().toString());
            fixedDeposit12.setProductName("Fixed Deposit 12 Months");
            fixedDeposit12.setCategory(ProductCategory.FIXED_DEPOSITS);
            fixedDeposit12.setInterestRate(new BigDecimal("6.50"));
            fixedDeposit12.setMinOperatingBalance(new BigDecimal("10000.00"));
            fixedDeposit12.setTermMonths(12L);

            Product fixedDeposit24 = new Product();
            fixedDeposit24.setId(UUID.randomUUID().toString());
            fixedDeposit24.setProductName("Fixed Deposit 24 Months");
            fixedDeposit24.setCategory(ProductCategory.FIXED_DEPOSITS);
            fixedDeposit24.setInterestRate(new BigDecimal("7.25"));
            fixedDeposit24.setMinOperatingBalance(new BigDecimal("20000.00"));
            fixedDeposit24.setTermMonths(24L);

            Product limitedAccessBasic = new Product();
            limitedAccessBasic.setId(UUID.randomUUID().toString());
            limitedAccessBasic.setProductName("Limited Access Basic");
            limitedAccessBasic.setCategory(ProductCategory.LIMITED_ACCESS);
            limitedAccessBasic.setInterestRate(new BigDecimal("5.75"));
            limitedAccessBasic.setMinOperatingBalance(new BigDecimal("3000.00"));

            Product limitedAccessPremium = new Product();
            limitedAccessPremium.setId(UUID.randomUUID().toString());
            limitedAccessPremium.setProductName("Limited Access Premium");
            limitedAccessPremium.setCategory(ProductCategory.LIMITED_ACCESS);
            limitedAccessPremium.setInterestRate(new BigDecimal("6.00"));
            limitedAccessPremium.setMinOperatingBalance(new BigDecimal("7000.00"));

            Product personalLoan = new Product();
            personalLoan.setId(UUID.randomUUID().toString());
            personalLoan.setProductName("Personal Loan");
            personalLoan.setCategory(ProductCategory.LOAN_ACCOUNT);
            personalLoan.setLoanCategory(LoanCategory.PERSONAL);
            personalLoan.setInterestRate(new BigDecimal("12.50"));

            Product homeLoan = new Product();
            homeLoan.setId(UUID.randomUUID().toString());
            homeLoan.setProductName("Home Loan");
            homeLoan.setCategory(ProductCategory.LOAN_ACCOUNT);
            homeLoan.setLoanCategory(LoanCategory.HOME);
            homeLoan.setInterestRate(new BigDecimal("8.50"));
            homeLoan.setMinOperatingBalance(BigDecimal.ZERO);


            Product educationLoan = new Product();
            educationLoan.setId(UUID.randomUUID().toString());
            educationLoan.setProductName("Education Loan");
            educationLoan.setCategory(ProductCategory.LOAN_ACCOUNT);
            educationLoan.setLoanCategory(LoanCategory.EDUCATION);
            educationLoan.setInterestRate(new BigDecimal("9.00"));
            personalLoan.setMinOperatingBalance(BigDecimal.ZERO);
            educationLoan.setMinOperatingBalance(BigDecimal.ZERO);

            productRepository.saveAll(List.of(
                    savingsBasic,
                    savingsPremium,
                    fixedDeposit12,
                    fixedDeposit24,
                    limitedAccessBasic,
                    limitedAccessPremium,
                    personalLoan,
                    homeLoan,
                    educationLoan
            ));
        }
    }
}