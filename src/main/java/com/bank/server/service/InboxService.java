package com.bank.server.service;

import com.bank.server.dto.InboxDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Inbox;
import com.bank.server.enums.LogType;
import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.InboxMessageType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.InboxNotFoundException;
import com.bank.server.mapper.InboxMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.InboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxService {
    private final InboxRepository inboxRepository;
    private final InboxMapper inboxMapper;
    private final LoggerService loggerService;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    //Confirm whether deposit requests should be enqueued by this service.
// Current understanding: deposits are initiated by the Real World Bank (RWB),
// so this method is temporarily disabled pending confirmation.

//    @PreAuthorize("hasRole('CUSTOMER')")
//    public PaymentResponseDTO enqueueDeposit( DepositRequestDTO depositRequestDTO)
//    {
//        log.info("Enqueuing deposit request for target account {}", depositRequestDTO.getTargetAccountId());
//        Inbox deposit = inboxMapper.toEntity(depositRequestDTO);
//
//        deposit.setId(UUID.randomUUID().toString());
//        deposit.setStatus(InboxStatus.PENDING);
//        deposit.setMessageType(InboxMessageType.DEPOSIT);
//
//        Inbox savedInbox =inboxRepository.save(deposit);
//
//        log.info("Deposit queue entry {} created successfully", savedInbox.getId());
//
//        loggerService.log(
//                "QUEUE_DEPOSIT",
//                "Deposit request queued successfully. Queue ID: " + savedInbox.getId(),
//                LogType.SUCCESS
//        );
//        return inboxMapper.toResponseDTO(savedInbox);
//    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public InboxResponseDTO enqueueWithdrawal(WithdrawRequestDTO withdrawRequestDTO)
    {
        log.info("Enqueuing withdraw request for target account {}", withdrawRequestDTO.getTargetAccountId());

        String correlationId = UUID.randomUUID().toString();

        Account account = accountRepository.findById(withdrawRequestDTO.getTargetAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "ACCOUNT_NOT_FOUND",
                        "Account not found for id " + withdrawRequestDTO.getTargetAccountId()));

        Map<String,Object> payload = new HashMap<>();
        payload.put("targetAccountId",withdrawRequestDTO.getTargetAccountId());
        payload.put("amount",withdrawRequestDTO.getAmount());

        String payloadJson;
        try{
            payloadJson = objectMapper.writeValueAsString(payload);
        }catch(JacksonException e){
            throw new RuntimeException("Failed to serialize withdrawal payload",e);
        }

        Inbox inbox = Inbox.builder()
                .id(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .payload(payloadJson)
                .status(InboxStatus.PENDING)
                .build();;

        Inbox savedInbox =inboxRepository.save(inbox);

        log.info("Withdrawal Inbox message {} created successfully", savedInbox.getId());

        loggerService.log(
                "QUEUE_WITHDRAWAL",
                "Withdrawal request queued successfully. Queue ID: " + savedInbox.getId(),
                LogType.SUCCESS
        );

        return InboxResponseDTO.builder()
                .id(savedInbox.getId())
                .status(savedInbox.getStatus())
                .message("Withdrawal request queued successfully.")
                .build();
    }

    public InboxDTO getInboxEntry(String id)
    {
        log.info("Fetching inbox queue entry {}", id);

        Inbox inboxEntry = inboxRepository.findById(id)
                .orElseThrow(()-> new InboxNotFoundException("GET_INBOX_ENTRY","Inbox queue entry not found for this id "+id));

        loggerService.log(
                "GET_INBOX_ENTRY",
                "Fetched inbox entry " + id,
                LogType.SUCCESS
        );

        return inboxMapper.toDTO(inboxEntry);
    }

    public List<Inbox> findPendingEntries(){
        return inboxRepository.findByStatus(InboxStatus.PENDING);
    }

    public List<InboxDTO> findPendingDeposits(){
        log.info("Fetching pending deposit queue entries");

        List<InboxDTO> pendingDeposits = inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(InboxStatus.PENDING, InboxMessageType.DEPOSIT)
                .stream()
                .map(inboxMapper::toDTO)
                .toList();
        loggerService.log(
                "FETCH_PENDING_DEPOSITS",
                "Fetched pending deposit inbox entries",
                LogType.SUCCESS
        );
        return pendingDeposits;
    }

    public List<InboxDTO> findPendingWithdrawals(){
        log.info("Fetching pending withdrawal queue entries");

        List<InboxDTO> pendingWithdrawals = inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(InboxStatus.PENDING, InboxMessageType.WITHDRAWAL_RESPONSE)
                .stream()
                .map(inboxMapper::toDTO)
                .toList();

        loggerService.log(
                "FETCH_PENDING_WITHDRAWALS",
                "Fetched pending withdrawals queue entries",
                LogType.SUCCESS
        );

        return pendingWithdrawals;
    }

    public void deleteById(String id)
    {
        log.info("Deleting inbox entry {}", id);

        Inbox inboxEntry =inboxRepository.findById(id)
                .orElseThrow(()-> new InboxNotFoundException("DELETE_INBOX_ENTRY","Inbox entry not found for this id "+id));

       inboxRepository.delete(inboxEntry);

        log.info("Deleted inbox entry with {} successfully", id);

        loggerService.log(
                "DELETE_INBOX_ENTRY",
                "Deleted inbox entry " + id,
                LogType.SUCCESS
        );
    }

    public InboxResponseDTO getInboxStatus(String id) {
        Inbox inbox =inboxRepository.findById(id)
                .orElseThrow(()-> new InboxNotFoundException("INBOX_NOT_FOUND", "Inbox entry not found for id "+id));

        return InboxResponseDTO.builder()
                .id(inbox.getId())
                .status(inbox.getStatus())
                .message("Payment request is pending.")
                .build();
    }
}
