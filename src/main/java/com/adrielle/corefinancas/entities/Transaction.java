package com.adrielle.corefinancas.entities;

import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento com o Utilizador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relacionamento com a Conta Bancária
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Relacionamento com a Categoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType type;

    // BigDecimal é a única forma segura de trabalhar com dinheiro em Java!
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionStatus status;

    @Column(name = "reference_id")
    private UUID referenceId; // Usado para ligar transferências entre duas contas

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", insertable = true, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Transaction() {}

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Account getAccount() {
        return account;
    }

    public Category getCategory() {
        return category;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}