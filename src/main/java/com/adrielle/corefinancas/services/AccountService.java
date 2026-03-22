package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    // Regra 1: Criar Conta
    public Account createAccount(AccountCreateDTO dto) {
        // Vai no banco confirmar se o utilizador existe. Se não existir, trava o processo.
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setName(dto.name());
        newAccount.setType(dto.type());
        
        // Se o front-end não mandar saldo, começa com zero por padrão
        newAccount.setBalance(dto.balance() != null ? dto.balance() : BigDecimal.ZERO);

        return accountRepository.save(newAccount);
    }

    // Regra 2: Listar as Contas de um Utilizador
    public List<Account> findAccountsByUser(UUID userId) {
        return accountRepository.findByUserId(userId);
    }
}