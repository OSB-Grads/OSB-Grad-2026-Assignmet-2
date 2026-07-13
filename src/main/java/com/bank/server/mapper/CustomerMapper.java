package com.bank.server.mapper;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO toDto(Customer customer);

    Customer toEntity(CustomerDTO customerDTO);
}