package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.AccountType;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountCreateDTO(
    UUID userId, 
    String name, 
    AccountType type, 
    BigDecimal balance
) {}