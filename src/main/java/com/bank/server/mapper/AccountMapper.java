package com.bank.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bank.server.dto.AccountDTO;
import com.bank.server.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "product.id", target = "productId")
    AccountDTO toDto(Account account);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    Account toEntity(AccountDTO dto);
}   