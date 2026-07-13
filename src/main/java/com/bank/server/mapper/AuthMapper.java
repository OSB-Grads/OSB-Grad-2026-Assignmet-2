package com.bank.server.mapper;

import com.bank.server.dto.AuthDTO;
import com.bank.server.entity.Auth;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthDTO toDto(Auth auth);
    Auth toEntity(AuthDTO authDTO);
}