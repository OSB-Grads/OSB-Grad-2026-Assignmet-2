package com.bank.server.mapper;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDTO toDto(Transaction transaction);

    Transaction toEntity(TransactionDTO transactionDTO);
}