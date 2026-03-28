package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.CategoryType;

import java.util.UUID;

public record CategoryResponseDTO(
    UUID id, 
    String name, 
    CategoryType type, 
    String colorHex, 
    UUID userId, 
    UUID parentId
) {
    public CategoryResponseDTO(com.adrielle.corefinancas.entities.Category c) {
        this(
            c.getId(), 
            c.getName(), 
            c.getType(), 
            c.getColorHex(), 
            c.getUser().getId(),
            c.getParent() != null ? c.getParent().getId() : null // Se não tiver pai, devolve null de forma segura
        );
    }
}