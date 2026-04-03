package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.dtos.AccountResponseDTO;
import com.adrielle.corefinancas.dtos.AccountUpdateDTO;
import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.services.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Endpoint para criar a conta
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountCreateDTO dto) {
        log.info("Requisição para criar conta: userId={}", dto.userId());
        Account savedAccount = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponseDTO(savedAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable UUID id, @RequestBody AccountUpdateDTO dto) {
        Account updatedAccount = accountService.updateAccount(id, dto);
        return ResponseEntity.ok(new AccountResponseDTO(updatedAccount));
    }

    // Endpoint para listar as contas com paginação
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponseDTO<AccountResponseDTO>> getUserAccounts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(accountService.findAccountsByUser(userId, pageable));
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<AccountResponseDTO> inactivateAccount(@PathVariable UUID id) {
        Account account = accountService.inactivateAccount(id);
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<AccountResponseDTO> reactivateAccount(@PathVariable UUID id) {
        Account account = accountService.reactivateAccount(id);
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }
}