package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CategoryRequestDTO(
    @NotNull(message = "userId é obrigatório")
    UUID userId,

    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotNull(message = "Tipo de categoria é obrigatório")
    CategoryType type,

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal (ex: #FF5733)")
    String colorHex,

    UUID parentId
) {}