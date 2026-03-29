package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.UserCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependência via construtor (Boas práticas do Spring)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserCreateDTO dto) {
        // Verifica se o email já existe para não quebrar o banco
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso!");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        // A MÁGICA DA SEGURANÇA ACONTECE AQUI:
        String encryptedPassword = passwordEncoder.encode(dto.password());
        newUser.setPasswordHash(encryptedPassword);// Encripta a senha antes de salvar

        return userRepository.save(newUser);
    }
}