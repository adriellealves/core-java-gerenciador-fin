package com.adrielle.corefinancas.repositories;

import com.adrielle.corefinancas.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);
    
    // Traz transações de uma conta específica
    List<Transaction> findByAccountId(UUID accountId);
}