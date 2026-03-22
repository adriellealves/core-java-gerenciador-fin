package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.UserCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO dto) {
        User savedUser = userService.createUser(dto);
        // Retorna o status 201 (Created) e o utilizador criado
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}