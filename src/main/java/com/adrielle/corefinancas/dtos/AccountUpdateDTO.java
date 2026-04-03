package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.AccountType;

import java.math.BigDecimal;

public record AccountUpdateDTO(
    String name,
    AccountType type,
    BigDecimal balance
) {}