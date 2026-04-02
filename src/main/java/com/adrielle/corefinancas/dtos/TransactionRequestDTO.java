package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequestDTO(
    @NotNull(message = "userId é obrigatório")
    UUID userId,

    @NotNull(message = "accountId é obrigatório")
    UUID accountId,

    @NotNull(message = "categoryId é obrigatório")
    UUID categoryId,

    @NotNull(message = "Tipo de transação é obrigatório")
    TransactionType type,

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal amount,

    @NotBlank(message = "Descrição é obrigatória")
    String description,

    @NotNull(message = "Data da transação é obrigatória")
    LocalDate transactionDate,

    @NotNull(message = "Status é obrigatório")
    TransactionStatus status,

    UUID referenceId
) {}