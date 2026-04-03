package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.dtos.CategoryResponseDTO;
import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.CategoryType;
import com.adrielle.corefinancas.exceptions.ResourceNotFoundException;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // 1. CRIAR
    @Transactional
    public Category createCategory(CategoryRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

        Category category = new Category();
        category.setUser(user);
        category.setName(dto.name());
        category.setType(dto.type());
        category.setColorHex(dto.colorHex());
        category.setActive(true);

        // Lógica de Subcategoria
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria pai não encontrada!"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        log.info("Categoria criada: id={}, userId={}, nome={}", saved.getId(), dto.userId(), dto.name());
        return saved;
    }

    // 2. EDITAR (Update)
    @Transactional
    public Category updateCategory(UUID categoryId, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));

        // Atualiza apenas os dados permitidos
        category.setName(dto.name());
        category.setType(dto.type());
        category.setColorHex(dto.colorHex());

        // Lógica para mudar o pai (opcional)
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria pai não encontrada!"));
            category.setParent(parent);
        } else {
            category.setParent(null); // Remove de subcategoria para categoria principal
        }

        log.info("Categoria atualizada: id={}", categoryId);
        return categoryRepository.save(category);
    }

    // 3. APAGAR (Soft Delete)
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Categoria inativada (soft delete): id={}", categoryId);
    }

    // 4. LISTAR TODAS (paginado, apenas ativas)
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    // 5. LISTAR POR USUÁRIO (paginado, com filtro opcional por tipo)
    @Transactional(readOnly = true)
    public PagedResponseDTO<CategoryResponseDTO> findCategoriesByUser(UUID userId, CategoryType type, Pageable pageable) {
        if (type != null) {
            return PagedResponseDTO.from(
                    categoryRepository.findByUserIdAndTypeAndActiveTrue(userId, type, pageable),
                    CategoryResponseDTO::new
            );
        }
        return PagedResponseDTO.from(
                categoryRepository.findByUserIdAndActiveTrue(userId, pageable),
                CategoryResponseDTO::new
        );
    }
}