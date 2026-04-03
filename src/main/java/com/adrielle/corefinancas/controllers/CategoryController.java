package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.dtos.CategoryResponseDTO;
import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.enums.CategoryType;
import com.adrielle.corefinancas.services.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        log.info("Requisição para criar categoria: userId={}", dto.userId());
        Category savedCategory = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoryResponseDTO(savedCategory));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.findAllCategories()
                .stream()
                .map(CategoryResponseDTO::new)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponseDTO<CategoryResponseDTO>> getUserCategories(
            @PathVariable UUID userId,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(categoryService.findCategoriesByUser(userId, type, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO dto) {
        Category updatedCategory = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(new CategoryResponseDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (padrão de sucesso para deleção)
    }
}