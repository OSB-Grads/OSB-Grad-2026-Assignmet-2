package com.bank.server.service;

import com.bank.server.dto.PaymentQueueDTO;
import com.bank.server.dto.request.DepositRequestDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.PaymentResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.PaymentQueue;
import com.bank.server.enums.LogType;
import com.bank.server.enums.PaymentStatus;
import com.bank.server.enums.PaymentType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.PaymentQueueNotFoundException;
import com.bank.server.mapper.PaymentQueueMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.PaymentQueueRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueService {
    private final PaymentQueueRepository paymentQueueRepository;
    private final PaymentQueueMapper paymentQueueMapper;
    private final LoggerService loggerService;
    private final AccountRepository accountRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentResponseDTO enqueueDeposit( DepositRequestDTO depositRequestDTO)
    {
        log.info("Enqueuing deposit request for target account {}", depositRequestDTO.getTargetAccountId());
        PaymentQueue deposit = paymentQueueMapper.toEntity(depositRequestDTO);

        deposit.setId(UUID.randomUUID().toString());
        deposit.setStatus(PaymentStatus.PENDING);
        deposit.setType(PaymentType.DEPOSIT);

        PaymentQueue savedPaymentQueue = paymentQueueRepository.save(deposit);

        log.info("Deposit queue entry {} created successfully", savedPaymentQueue.getId());

        loggerService.log(
                "QUEUE_DEPOSIT",
                "Deposit request queued successfully. Queue ID: " + savedPaymentQueue.getId(),
                LogType.SUCCESS
        );
        return paymentQueueMapper.toResponseDTO(savedPaymentQueue);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentResponseDTO enqueueWithdrawal(WithdrawRequestDTO withdrawRequestDTO)
    {
        log.info("Enqueuing withdraw request for target account {}", withdrawRequestDTO.getTargetAccountId());

        Account account = accountRepository.findById(withdrawRequestDTO.getTargetAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "ACCOUNT_NOT_FOUND",
                        "Account not found for id " + withdrawRequestDTO.getTargetAccountId()));

        PaymentQueue withdraw = paymentQueueMapper.toEntity(withdrawRequestDTO);

        withdraw.setId(UUID.randomUUID().toString());
        withdraw.setStatus(PaymentStatus.PENDING);
        withdraw.setType(PaymentType.WITHDRAW);

        PaymentQueue savedPaymentQueue = paymentQueueRepository.save(withdraw);

        log.info("Withdrawal queue entry {} created successfully", savedPaymentQueue.getId());

        loggerService.log(
                "QUEUE_WITHDRAWAL",
                "Withdrawal request queued successfully. Queue ID: " + savedPaymentQueue.getId(),
                LogType.SUCCESS
        );

        return paymentQueueMapper.toResponseDTO(savedPaymentQueue);
    }

    public PaymentQueueDTO getQueueEntry(String id)
    {
        log.info("Fetching payment queue entry {}", id);

        PaymentQueue queueEntry = paymentQueueRepository.findById(id)
                .orElseThrow(()-> new PaymentQueueNotFoundException("GET_PAYMENT_QUEUE_ENTRY","Payment queue entry not found for this id "+id));

        loggerService.log(
                "GET_PAYMENT_QUEUE_ENTRY",
                "Fetched payment queue entry " + id,
                LogType.SUCCESS
        );

        return paymentQueueMapper.toDTO(queueEntry);
    }

    public List<PaymentQueue> findPendingEntries(){
        return paymentQueueRepository.findByStatus(PaymentStatus.PENDING);
    }

    public List<PaymentQueueDTO> findPendingDeposits(){
        log.info("Fetching pending deposit queue entries");

        List<PaymentQueueDTO> pendingDeposits = paymentQueueRepository.findByStatusAndType(PaymentStatus.PENDING,PaymentType.DEPOSIT)
                .stream()
                .map(paymentQueueMapper::toDTO)
                .toList();
        loggerService.log(
                "FETCH_PENDING_DEPOSITS",
                "Fetched pending deposit queue entries",
                LogType.SUCCESS
        );
        return pendingDeposits;
    }

    public List<PaymentQueueDTO> findPendingWithdrawals(){
        log.info("Fetching pending withdrawal queue entries");

        List<PaymentQueueDTO> pendingWithdrawals = paymentQueueRepository.findByStatusAndType(PaymentStatus.PENDING,PaymentType.WITHDRAW)
                .stream()
                .map(paymentQueueMapper::toDTO)
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
        log.info("Deleting payment queue entry {}", id);

        PaymentQueue queueEntry = paymentQueueRepository.findById(id)
                .orElseThrow(()-> new PaymentQueueNotFoundException("DELETE_PAYMENT_QUEUE_ENTRY","Payment queue entry not found for this id "+id));

        paymentQueueRepository.delete(queueEntry);

        log.info("Deleted payment queue entry with {} successfully", id);

        loggerService.log(
                "DELETE_PAYMENT_QUEUE_ENTRY",
                "Deleted payment queue entry " + id,
                LogType.SUCCESS
        );
    }

    public PaymentResponseDTO getPaymentStatus(String id) {
        PaymentQueue paymentQueue = paymentQueueRepository.findById(id)
                .orElseThrow(()-> new PaymentQueueNotFoundException("PAYMENT_QUEUE_NOT_FOUND", "payment Queue not found for id "+id));

        PaymentResponseDTO paymentResponseDTO = paymentQueueMapper.toResponseDTO(paymentQueue);
        paymentResponseDTO.setMessage("Payment request is pending.");
        return paymentResponseDTO;
    }
}
