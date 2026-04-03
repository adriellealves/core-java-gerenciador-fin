package com.adrielle.corefinancas.repositories;

import com.adrielle.corefinancas.entities.Transaction;
import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);

    // Traz transações de uma conta específica
    List<Transaction> findByAccountId(UUID accountId);

    Page<Transaction> findByUserIdAndActiveTrue(UUID userId, Pageable pageable);
    Page<Transaction> findByUserIdAndTypeAndActiveTrue(UUID userId, TransactionType type, Pageable pageable);
    Page<Transaction> findByUserIdAndStatusAndActiveTrue(UUID userId, TransactionStatus status, Pageable pageable);
    Page<Transaction> findByUserIdAndTypeAndStatusAndActiveTrue(UUID userId, TransactionType type, TransactionStatus status, Pageable pageable);
    Page<Transaction> findByAccountIdAndActiveTrue(UUID accountId, Pageable pageable);
}