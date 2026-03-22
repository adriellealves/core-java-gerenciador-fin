package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.UserCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Injeção de dependência via construtor (Boas práticas do Spring)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserCreateDTO dto) {
        // Verifica se o email já existe para não quebrar o banco
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso!");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        newUser.setPasswordHash(dto.password()); // No futuro, vamos encriptar isto!

        return userRepository.save(newUser);
    }
}