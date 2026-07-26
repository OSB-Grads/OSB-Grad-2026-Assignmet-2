package com.bank.server.service;

import com.bank.server.entity.Customer;
import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.IllegalArgumentException;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LogRepository;
import com.bank.server.utils.AuthenticationUtil;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        logEntry.setId(Generator.generateUuid());
         
      String customerId = AuthenticationUtil.getCurrentCustomerIdOrNull();
        if(customerId != null) {
            Customer customer = customerRepository.findById(customerId)
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
    }
}