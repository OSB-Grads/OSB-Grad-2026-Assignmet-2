package com.bank.server.repository;

import com.bank.server.entity.PaymentQueue;
import com.bank.server.enums.PaymentStatus;
import com.bank.server.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentQueueRepository extends JpaRepository<PaymentQueue,String> {

    List<PaymentQueue> findByStatus(PaymentStatus status);

    List<PaymentQueue> findByStatusAndType(PaymentStatus status, PaymentType type);
}
