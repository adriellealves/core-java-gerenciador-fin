package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequestDTO(
    UUID userId,
    UUID accountId,
    UUID categoryId,
    TransactionType type,
    BigDecimal amount,
    String description,
    LocalDate transactionDate,
    TransactionStatus status
) {}