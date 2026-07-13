package com.bank.server.repository;

import com.bank.server.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository
        extends JpaRepository<LogEntry, String> {
}