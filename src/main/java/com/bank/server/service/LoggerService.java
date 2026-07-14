package com.bank.server.service;

import com.bank.server.dto.response.LogResponse;
import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.mapper.LogMapper;
import com.bank.server.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.exception.IllegalArgumentException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LoggerService {

    private final LogRepository logEntryRepository;
    private final LogMapper logMapper;

    @Transactional
    public LogResponse log(LogResponse logDTO) {

        if (logDTO == null) {
            throw new IllegalArgumentException(
                    "Log DTO cannot be null"
            );
        }
        if (logDTO.getAction() == null
                || logDTO.getAction().isBlank()) {
            throw new IllegalArgumentException(
                    "Log action cannot be empty"
            );
        }

        LogEntry logEntry = logMapper.toEntity(logDTO);

        logEntry.setId(UUID.randomUUID().toString());

        if (logEntry.getStatus() == null) {
            logEntry.setStatus(LogType.SUCCESS);
        }

        LogEntry savedLogEntry =
                logEntryRepository.save(logEntry);

        return logMapper.toDTO(savedLogEntry);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<LogResponse> getAllLogs() {

        return logEntryRepository.findAll()
                .stream()
                .map(logMapper::toDTO)
                .toList();
    }
}