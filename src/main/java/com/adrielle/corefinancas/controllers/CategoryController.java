package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.dtos.CategoryResponseDTO;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto) {
        Category savedCategory = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoryResponseDTO(savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID id, @RequestBody CategoryRequestDTO dto) {
        Category updatedCategory = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(new CategoryResponseDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (padrão de sucesso para deleção)
    }
}
