package com.bank.server.service;

import com.bank.server.dto.PaymentQueueDTO;
import com.bank.server.entity.PaymentQueue;
import com.bank.server.enums.LogType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final LoggerService loggerService;
    private final PaymentQueueService paymentQueueService;
    private final PaymentProcessorService paymentProcessorService;

    public void processDeposits(){
        processPayments(paymentQueueService.findPendingDeposits());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void processWithdrawals(){
        processPayments(paymentQueueService.findPendingWithdrawals());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void processPayments(List<PaymentQueueDTO> pendingEntries){
        for(PaymentQueueDTO entry : pendingEntries)
        {
            log.info("Processing {} request for entry {}",entry.getType(),entry.getId());


            switch (entry.getType()){
                    case DEPOSIT : paymentProcessorService.processDeposit(entry);
                        break;

                    case WITHDRAW : paymentProcessorService.processWithdrawal(entry);
                        break;

                    default:
                        throw  new IllegalStateException("Unsupported payment type "+entry.getType());
            }
            loggerService.log(
                    "PAYMENT_PROCESS",
                    entry.getType() + " processed successfully",
                    LogType.SUCCESS
            );
        }
    }
}
