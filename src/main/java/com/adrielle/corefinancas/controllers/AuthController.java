package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.LoginRequestDTO;
import com.adrielle.corefinancas.dtos.LoginResponseDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.UserRepository;
import com.adrielle.corefinancas.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body) {
        // 1. Procura o usuário no banco pelo E-mail
        User user = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Compara a senha digitada com a senha criptografada do banco
        // O passwordEncoder.matches é inteligente e consegue bater o texto puro com o Hash BCrypt!
        if (passwordEncoder.matches(body.password(), user.getPasswordHash())) {
            
            // 3. Se a senha bater, gera o Token JWT
            String token = tokenService.generateToken(user);
            
            // 4. Devolve o token e os dados básicos para o Frontend
            return ResponseEntity.ok(new LoginResponseDTO(token, user.getId().toString(), user.getName()));
        }
        
        // Se a senha estiver errada, devolve erro 400 (Bad Request)
        return ResponseEntity.badRequest().build();
    }
}