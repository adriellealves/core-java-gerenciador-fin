package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDTO(
    UUID id,                 // O ID da transação que acabou de nascer
    String description,
    BigDecimal amount,
    TransactionType type,
    TransactionStatus status,
    LocalDate transactionDate,
    UUID accountId,          // Veja a mágica: Devolvemos SÓ O ID da conta!
    UUID categoryId          // E SÓ O ID da categoria!
) {
    // Podemos criar um construtor inteligente que converte a Entidade neste DTO
    public TransactionResponseDTO(com.adrielle.corefinancas.entities.Transaction t) {
        this(
            t.getId(),
            t.getDescription(),
            t.getAmount(),
            t.getType(),
            t.getStatus(),
            t.getTransactionDate(),
            t.getAccount().getId(),
            t.getCategory().getId()
        );
    }
}