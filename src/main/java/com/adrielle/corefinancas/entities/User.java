package com.adrielle.corefinancas.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Relacionamento: Um Usuário tem Várias Contas (Cai muito em prova!)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;

    public User() {}
    // Lembre-se de gerar os Getters e Setters no VS Code (Botão direito -> Source Action)

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    

}