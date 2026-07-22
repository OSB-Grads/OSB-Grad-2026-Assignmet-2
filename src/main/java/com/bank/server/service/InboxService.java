package com.bank.server.service;

import com.bank.server.dto.InboxDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.entity.Inbox;
import com.bank.server.enums.LogType;
import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.InboxMessageType;
import com.bank.server.exception.InboxNotFoundException;
import com.bank.server.mapper.InboxMapper;
import com.bank.server.repository.InboxRepository;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxService {
    private final InboxRepository inboxRepository;
    private final InboxMapper inboxMapper;
    private final LoggerService loggerService;
    private final ObjectMapper objectMapper;

    //To Confirm whether deposit requests should be enqueued by this service.
// Current understanding: deposits are initiated by the Real World Bank (RWB),
// so this method is temporarily disabled pending confirmation.

    @PreAuthorize("hasRole('CUSTOMER')")
    public InboxResponseDTO enqueueWithdrawal(WithdrawRequestDTO withdrawRequestDTO,String transactionId,String nationalId)
    {
        log.info("Enqueuing withdraw request for target account {}", withdrawRequestDTO.getTargetAccountId());

        Map<String,Object> payload = new HashMap<>();
        payload.put("targetAccountId",withdrawRequestDTO.getTargetAccountId());
        payload.put("amount",withdrawRequestDTO.getAmount());
        payload.put("customerIdentityNumber",nationalId);

        String payloadJson;
        try{
            payloadJson = objectMapper.writeValueAsString(payload);
        }catch(JacksonException e){
            throw new RuntimeException("Failed to serialize withdrawal payload",e);
        }

        Inbox inbox = Inbox.builder()
                .id(Generator.generateUuid())
                .correlationId(Generator.generateUuid())
                .transactionId(transactionId)
                .messageType(InboxMessageType.WITHDRAWAL)
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
                .transactionId(transactionId)
                .status(savedInbox.getStatus())
                .message("Withdrawal request queued successfully.")
                .build();
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

    public List<InboxDTO> findPendingWithdrawalResponses(){
        log.info("Fetching pending withdrawal response inbox entries");

        List<InboxDTO> pendingWithdrawalResponses = inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(InboxStatus.PENDING, InboxMessageType.WITHDRAWAL_RESPONSE)
                .stream()
                .map(inboxMapper::toDTO)
                .toList();

        loggerService.log(
                "FETCH_PENDING_WITHDRAWAL_RESPONSES",
                "Fetched pending withdrawals response inbox entries",
                LogType.SUCCESS
        );

        return pendingWithdrawalResponses;
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

}
