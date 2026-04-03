package com.adrielle.corefinancas.repositories;

import com.adrielle.corefinancas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Magia do Spring: Só de escrever este nome, ele cria o SQL: 
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}