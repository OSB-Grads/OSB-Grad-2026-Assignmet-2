package com.bank.server.mapper;

import com.bank.server.dto.PaymentQueueDTO;
import com.bank.server.dto.request.DepositRequestDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.PaymentResponseDTO;
import com.bank.server.entity.PaymentQueue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentQueueMapper {

    PaymentQueueDTO toDTO(PaymentQueue paymentQueue);

    PaymentQueue toEntity(PaymentQueueDTO paymentQueueDTO);

    PaymentQueue toEntity(DepositRequestDTO depositRequestDTO);

    PaymentQueue toEntity(WithdrawRequestDTO withdrawRequestDTO);

    PaymentResponseDTO toResponseDTO(PaymentQueue paymentQueue);
}
