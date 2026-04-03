package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.UserCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.exceptions.DuplicateResourceException;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependência via construtor (Boas práticas do Spring)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(UserCreateDTO dto) {
        // Verifica se o email já existe para não quebrar o banco
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new DuplicateResourceException("E-mail já está em uso!");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        // A MÁGICA DA SEGURANÇA ACONTECE AQUI:
        String encryptedPassword = passwordEncoder.encode(dto.password());
        newUser.setPasswordHash(encryptedPassword);// Encripta a senha antes de salvar

        User saved = userRepository.save(newUser);
        log.info("Novo usuário criado: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }
}