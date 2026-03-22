package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.dtos.AccountResponseDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Endpoint para criar a conta
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountCreateDTO dto) {
        Account savedAccount = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponseDTO(savedAccount));
    }
    // Endpoint para listar as contas (Ex: GET /api/accounts/user/123e4567-e89b-12d3...)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDTO>> getUserAccounts(@PathVariable UUID userId) {
        // Uso da API de Streams do Java (Cai muito em prova!)
        List<AccountResponseDTO> accounts = accountService.findAccountsByUser(userId)
                .stream()
                .map(AccountResponseDTO::new) // Converte cada Entidade num DTO
                .toList();
                
        return ResponseEntity.ok(accounts);
    }
}