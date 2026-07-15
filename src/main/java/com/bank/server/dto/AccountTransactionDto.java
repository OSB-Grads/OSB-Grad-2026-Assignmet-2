package com.bank.server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountTransactionDto {
    private ViewAccountResponseDTO viewAccountResponseDTO;
    private List<TransactionDTO> transactiondto;
}
