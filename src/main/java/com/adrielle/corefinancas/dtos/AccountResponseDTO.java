package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.AccountType;
import com.adrielle.corefinancas.entities.Account;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
    UUID id, 
    String name, 
    AccountType type, 
    BigDecimal balance, 
    UUID userId // Apenas o ID!
) {
    public AccountResponseDTO(Account a) {
        this(a.getId(), a.getName(), a.getType(), a.getBalance(), a.getUser().getId());
    }
}