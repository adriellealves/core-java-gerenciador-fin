package com.adrielle.corefinancas.dtos;

import com.adrielle.corefinancas.enums.CategoryType;
import java.util.UUID;

public record CategoryRequestDTO(
    UUID userId, 
    String name, 
    CategoryType type, 
    String colorHex, 
    UUID parentId // Se for null, é uma categoria principal. Se tiver ID, é subcategoria!
) {}