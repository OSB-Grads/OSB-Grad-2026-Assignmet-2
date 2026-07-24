package com.bank.server.config;

import com.bank.server.entity.Auth;
import com.bank.server.entity.Customer;
import com.bank.server.repository.AuthRepository;
import com.bank.server.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Order(3)
@RequiredArgsConstructor
public class CustomerSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AuthRepository authRepository;

    @Override
    public void run(String... args) {
        Auth shivakumarAuth = authRepository.findByUsername("shivakumar")
                .orElseThrow();

        Auth charuAuth = authRepository.findByUsername("charu")
                .orElseThrow();

        Auth akashAuth = authRepository.findByUsername("akash")
                .orElseThrow();
        Auth adminAuth = authRepository.findByUsername("admin")
                .orElseThrow();

        if (customerRepository.count() == 0) {

            Customer shivakumar = new Customer();
            shivakumar.setId(shivakumarAuth.getId());
            shivakumar.setFirstName("Shivakumar");
            shivakumar.setLastName("Khot");
            shivakumar.setDateOfBirth("2001-04-15");
            shivakumar.setEmail("shivakumar@example.com");
            shivakumar.setPhone("9876543210");
            shivakumar.setAddress("Bengaluru");
            shivakumar.setNationalId("NI10001");

            Customer charu = new Customer();
            charu.setId(charuAuth.getId());
            charu.setFirstName("Charu");
            charu.setLastName("Bohra");
            charu.setDateOfBirth("1999-08-20");
            charu.setEmail("charu@example.com");
            charu.setPhone("9876543211");
            charu.setAddress("Jaipur");
            charu.setNationalId("NI10002");

            Customer akash = new Customer();
            akash.setId(akashAuth.getId());
            akash.setFirstName("Akash");
            akash.setLastName("Shakrayya");
            akash.setDateOfBirth("1998-12-10");
            akash.setEmail("akash@example.com");
            akash.setPhone("9876543212");
            akash.setAddress("Bengaluru");
            akash.setNationalId("NI10003");

            Customer admin = new Customer();
            admin.setId(adminAuth.getId());
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setDateOfBirth("1998-12-10");
            admin.setEmail("admin@example.com");
            admin.setPhone("9876543212");
            admin.setAddress("Bengaluru");
            admin.setNationalId("NI10003");

            customerRepository.saveAll(List.of(
                    admin,
                    shivakumar,
                    charu,
                    akash
            ));
        }
    }
}