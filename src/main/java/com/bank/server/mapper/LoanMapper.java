package com.bank.server.mapper;

import com.bank.server.dto.LoanDTO;
import com.bank.server.dto.request.LoanRequestDTO;
import com.bank.server.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "loanCategory.id", target = "loanCategoryId")
    LoanDTO toDto(Loan loan);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "loanCategory", ignore = true)
    @Mapping(target = "disbursementAccount", ignore = true)
    Loan toEntity(LoanDTO loanDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "loanCategory", ignore = true)
    @Mapping(target = "disbursementAccount", ignore = true)
    @Mapping(target = "maxEligibleAmount", ignore = true)
    @Mapping(target = "offeredRate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Loan toEntity(LoanRequestDTO request);
}