package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.dtos.TransactionRequestDTO;
import com.adrielle.corefinancas.dtos.TransactionResponseDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.entities.Transaction;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import com.adrielle.corefinancas.exceptions.BusinessRuleException;
import com.adrielle.corefinancas.exceptions.InsufficientBalanceException;
import com.adrielle.corefinancas.exceptions.ResourceNotFoundException;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.TransactionRepository;
import com.adrielle.corefinancas.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, 
                              AccountRepository accountRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO dto) {
        // 1. Busca todo mundo no banco de dados
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
                
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada!"));
                
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));

        // 2. Validações de regra de negócio (amount é validado pelo @Positive no DTO)
        if (dto.type() == TransactionType.TRANSFER && dto.referenceId() == null) {
            throw new BusinessRuleException("Transferências devem informar o referenceId da transação vinculada.");
        }

        // 3. Monta a transação
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setStatus(dto.status());
        transaction.setActive(true);

        if (dto.referenceId() != null) {
            transaction.setReferenceId(dto.referenceId());
        }

        // --- REGRA DE NEGÓCIO: CÁLCULO DE SALDO ---
        // Vamos alterar o saldo da conta APENAS se a transação já estiver PAGA (PAID)
        if (transaction.getStatus() == TransactionStatus.PAID) {
            if (transaction.getType() == TransactionType.INCOME) {
                // Se for Receita, SOMAMOS (+)
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                // Se for Despesa, SUBTRAÍMOS (-) com validação de saldo suficiente
                if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                    throw new InsufficientBalanceException("Saldo insuficiente na conta selecionada.");
                }
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
            // Salva o novo saldo da conta no banco de dados
            accountRepository.save(account);
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transação criada: id={}, tipo={}, valor={}, userId={}", saved.getId(), dto.type(), dto.amount(), dto.userId());
        return saved;
    }

    // Método para listar transações com paginação e filtros opcionais
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> findTransactionsByUser(
            UUID userId, TransactionType type, TransactionStatus status, Pageable pageable) {

        if (type != null && status != null) {
            return PagedResponseDTO.from(
                    transactionRepository.findByUserIdAndTypeAndStatusAndActiveTrue(userId, type, status, pageable),
                    TransactionResponseDTO::new
            );
        } else if (type != null) {
            return PagedResponseDTO.from(
                    transactionRepository.findByUserIdAndTypeAndActiveTrue(userId, type, pageable),
                    TransactionResponseDTO::new
            );
        } else if (status != null) {
            return PagedResponseDTO.from(
                    transactionRepository.findByUserIdAndStatusAndActiveTrue(userId, status, pageable),
                    TransactionResponseDTO::new
            );
        }
        return PagedResponseDTO.from(
                transactionRepository.findByUserIdAndActiveTrue(userId, pageable),
                TransactionResponseDTO::new
        );
    }
}