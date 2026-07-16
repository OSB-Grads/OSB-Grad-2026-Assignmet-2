package com.bank.server.mapper;

import com.bank.server.dto.response.LogResponse;
import com.bank.server.entity.LogEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    @Mapping(source = "customer.id", target = "customerId")
    LogResponse toDTO(LogEntry logEntry);

    @Mapping(target = "customer", ignore = true)
    LogEntry toEntity(LogResponse logResponse);
}