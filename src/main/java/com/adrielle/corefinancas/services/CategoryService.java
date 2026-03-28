package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // 1. CRIAR
    public Category createCategory(CategoryRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        Category category = new Category();
        category.setUser(user);
        category.setName(dto.name());
        category.setType(dto.type());
        category.setColorHex(dto.colorHex());

        // Lógica de Subcategoria
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new RuntimeException("Categoria pai não encontrada!"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    // 2. EDITAR (Update)
    public Category updateCategory(UUID categoryId, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

        // Atualiza apenas os dados permitidos
        category.setName(dto.name());
        category.setType(dto.type());
        category.setColorHex(dto.colorHex());

        // Lógica para mudar o pai (opcional)
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new RuntimeException("Categoria pai não encontrada!"));
            category.setParent(parent);
        } else {
            category.setParent(null); // Remove de subcategoria para categoria principal
        }

        return categoryRepository.save(category);
    }

    // 3. APAGAR (Delete)
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoria não encontrada!");
        }
        categoryRepository.deleteById(categoryId);
    }

    // 4. LISTAR TODAS
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    // 5. LISTAR POR USUÁRIO
    public List<Category> findCategoriesByUser(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }
}
