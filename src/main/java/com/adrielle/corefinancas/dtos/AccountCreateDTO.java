package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountCreateDTO(
    @NotNull(message = "userId é obrigatório")
    UUID userId,

    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotNull(message = "Tipo de conta é obrigatório")
    AccountType type,

    @PositiveOrZero(message = "Saldo inicial não pode ser negativo")
    BigDecimal balance
) {}