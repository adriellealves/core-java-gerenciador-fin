package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.TransactionRequestDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.entities.Category;
import com.adrielle.corefinancas.entities.Transaction;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.TransactionType;
import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.TransactionRepository;
import com.adrielle.corefinancas.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

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

    public Transaction createTransaction(TransactionRequestDTO dto) {
        // 1. Busca todo mundo no banco de dados
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
                
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));
                
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

        // 2. Monta a transação
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setStatus(dto.status());

        // --- INÍCIO DA NOVA REGRA DE NEGÓCIO (CÁLCULO DE SALDO) ---
        // Vamos alterar o saldo da conta APENAS se a transação já estiver PAGA (PAID)

        if (transaction.getStatus() == TransactionStatus.PAID) {
            if (transaction.getType() == TransactionType.INCOME) {
                // Se for Receita, SOMAMOS (+)
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                // Se for Despesa, SUBTRAÍMOS (-)
                if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente na conta selecionada.");
                
                }
                 // Se passou da validação, subtrai o saldo
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
            // Salva o novo saldo da conta no banco de dados
            accountRepository.save(account);
        }
        // --- FIM DA REGRA DE NEGÓCIO ---

        // 3. Salva no banco
        return transactionRepository.save(transaction);
    }

    // Método bônus essencial para a sua tela principal do Front-end:
    public List<Transaction> findTransactionsByUser(UUID userId) {
        return transactionRepository.findByUserId(userId);
    }
}