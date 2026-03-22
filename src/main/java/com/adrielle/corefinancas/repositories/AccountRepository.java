package com.adrielle.corefinancas.repositories;

import com.adrielle.corefinancas.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    // Traz todas as contas de um utilizador específico
    List<Account> findByUserId(UUID userId);
}