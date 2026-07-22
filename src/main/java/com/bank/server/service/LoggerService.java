package com.bank.server.service;

import com.bank.server.entity.Customer;
import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LogRepository;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.exception.IllegalArgumentException;
import java.util.UUID;
import com.bank.server.utils.AuthenticationUtil;


@RequiredArgsConstructor
@Service
public class LoggerService {

    private final CustomerRepository customerRepository;
    private final LogRepository logEntryRepository;
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
        logEntry.setId(UUID.randomUUID().toString());
        logEntry.setId(Generator.generateUuid());

        String customerId = AuthenticationUtil.getCurrentCustomerId();
        Customer customer = customerRepository.findById(customerId).orElse(null);

        logEntry.setCustomer(customer);
        logEntry.setAction(action);
        logEntry.setDetails(message);
        logEntry.setStatus(
                status != null ? status : LogType.SUCCESS
        );

        logEntryRepository.save(logEntry);
    }}
