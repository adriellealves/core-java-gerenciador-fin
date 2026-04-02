package com.adrielle.corefinancas.repositories;

import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    Page<Category> findByUserIdAndActiveTrue(UUID userId, Pageable pageable);
    Page<Category> findByUserIdAndTypeAndActiveTrue(UUID userId, CategoryType type, Pageable pageable);
}