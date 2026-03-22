package com.adrielle.corefinancas.entities;

import com.adrielle.corefinancas.enums.CategoryType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Auto-relacionamento: Uma subcategoria aponta para uma categoria pai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoryType type;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public Category() {}
    // Gerar Getters e Setters!

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Category getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public String getColorHex() {
        return colorHex;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    
}