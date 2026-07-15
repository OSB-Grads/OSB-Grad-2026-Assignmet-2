package com.bank.server.config;

import com.bank.server.entity.Auth;
import com.bank.server.enums.Role;
import com.bank.server.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class AuthSeeder implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (authRepository.count() == 0) {

            Auth admin = new Auth();
            admin.setId(UUID.randomUUID().toString());
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);

            Auth shivakumar = new Auth();
            shivakumar.setId(UUID.randomUUID().toString());
            shivakumar.setUsername("shivakumar");
            shivakumar.setPasswordHash(passwordEncoder.encode("Shiva@123"));
            shivakumar.setRole(Role.CUSTOMER);

            Auth charu = new Auth();
            charu.setId(UUID.randomUUID().toString());
            charu.setUsername("charu");
            charu.setPasswordHash(passwordEncoder.encode("Charu@123"));
            charu.setRole(Role.CUSTOMER);

            Auth akash = new Auth();
            akash.setId(UUID.randomUUID().toString());
            akash.setUsername("akash");
            akash.setPasswordHash(passwordEncoder.encode("Akash@123"));
            akash.setRole(Role.CUSTOMER);

            authRepository.saveAll(List.of(
                    admin,
                    shivakumar,
                    charu,
                    akash
            ));
        }
    }
}