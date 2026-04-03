package com.adrielle.corefinancas.controllers;

import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.dtos.TransactionRequestDTO;
import com.adrielle.corefinancas.dtos.TransactionResponseDTO;
import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import com.adrielle.corefinancas.entities.Transaction;
import com.adrielle.corefinancas.services.TransactionService;
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
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        log.info("Requisição para criar transação: userId={}, tipo={}", dto.userId(), dto.type());
        // O Service continua devolvendo a Entidade
        Transaction savedTransaction = transactionService.createTransaction(dto);
        
        // Nós convertemos para o DTO limpo
        TransactionResponseDTO response = new TransactionResponseDTO(savedTransaction);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> getUserTransactions(
            @PathVariable UUID userId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(transactionService.findTransactionsByUser(userId, type, status, pageable));
    }
}
