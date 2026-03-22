package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.TransactionRequestDTO;
import com.adrielle.corefinancas.dtos.TransactionResponseDTO;
import com.adrielle.corefinancas.entities.Transaction;
import com.adrielle.corefinancas.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO dto) {
        // O Service continua devolvendo a Entidade
        Transaction savedTransaction = transactionService.createTransaction(dto);
        
        // Nós convertemos para o DTO limpo
        TransactionResponseDTO response = new TransactionResponseDTO(savedTransaction);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable UUID userId) {
        return ResponseEntity.ok(transactionService.findTransactionsByUser(userId));
    }
}
