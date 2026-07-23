package com.bank.server.service;

import com.bank.server.entity.Customer;
import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LogRepository;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.exception.IllegalArgumentException;

import java.util.Optional;
import java.util.UUID;
import com.bank.server.utils.AuthenticationUtil;


@RequiredArgsConstructor
@Service
public class LoggerService {

    private final LogRepository logEntryRepository;
    private final CustomerRepository customerRepository;
    Authentication authentication;
    @Transactional
    public void log(String action,
                    String message,
                    LogType status) {

        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException(
                    "Log action cannot be empty"
            );
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Log message cannot be empty"
            );
        }

        LogEntry logEntry = new LogEntry();
        logEntry.setId(Generator.generateUuid());
        String customerId = AuthenticationUtil.getCurrentCustomerId();
        if(customerId != null) {
            Customer customer = customerRepository.findById(AuthenticationUtil.getCurrentCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException("CUSTOMER_FETCH",
                            "Customer not found"));
            logEntry.setCustomer(customer);
        }else {
            logEntry.setCustomer(null);
        }
        logEntry.setAction(action);
        logEntry.setDetails(message);
        logEntry.setStatus(
                status != null ? status : LogType.SUCCESS
        );
        logEntryRepository.save(logEntry);
    }}
