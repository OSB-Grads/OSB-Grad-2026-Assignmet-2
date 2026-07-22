package com.bank.server.repository;

import com.bank.server.entity.Inbox;
import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.InboxMessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxRepository extends JpaRepository<Inbox,String> {

    List<Inbox> findByStatus(InboxStatus status);

    List<Inbox> findByStatusAndMessageTypeOrderByCreatedAtAsc(InboxStatus status, InboxMessageType messageType);
}
