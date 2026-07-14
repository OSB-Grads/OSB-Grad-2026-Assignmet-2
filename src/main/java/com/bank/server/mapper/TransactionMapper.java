package com.bank.server.mapper;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "fromAccount.id", target = "fromAccountId")
    @Mapping(source = "toAccount.id", target = "toAccountId")
    TransactionDTO toDto(Transaction transaction);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "fromAccount", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    Transaction toEntity(TransactionDTO transactionDTO);
}