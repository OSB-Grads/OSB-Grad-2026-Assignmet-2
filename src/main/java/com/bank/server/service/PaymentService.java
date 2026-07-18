package com.bank.server.service;

import com.bank.server.dto.InboxDTO;
import com.bank.server.enums.LogType;
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
    private final InboxService inboxService;
    private final PaymentProcessorService paymentProcessorService;

    @PreAuthorize("hasRole('ADMIN')")
    public void processDeposits(){
        processPayments(inboxService.findPendingDeposits());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void processWithdrawals(){
        processPayments(inboxService.findPendingWithdrawals());
    }

    private void processPayments(List<InboxDTO> pendingEntries){
        for(InboxDTO entry : pendingEntries)
        {
            log.info("Processing {} request for entry {}",entry.getMessageType(),entry.getId());

            switch (entry.getMessageType()){
                    case DEPOSIT : paymentProcessorService.processDeposit(entry);
                        break;

                    case WITHDRAWAL_RESPONSE: paymentProcessorService.processWithdrawal(entry);
                        break;

                    default:
                        throw  new IllegalStateException("Unsupported payment type "+entry.getMessageType());
            }
            loggerService.log(
                    "PAYMENT_PROCESS",
                    entry.getMessageType() + " processed successfully for inbox "+entry.getId(),
                    LogType.SUCCESS
            );
        }
    }
}
