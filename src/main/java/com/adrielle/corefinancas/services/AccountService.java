package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.dtos.AccountUpdateDTO;
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
        newAccount.setActive(true);
        
        // Se o front-end não mandar saldo, começa com zero por padrão
        newAccount.setBalance(dto.balance() != null ? dto.balance() : BigDecimal.ZERO);

        return accountRepository.save(newAccount);
    }

    // Regra 2: Listar as Contas de um Utilizador
    public List<Account> findAccountsByUser(UUID userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId);
    }

    // Regra 3: Editar Conta
    public Account updateAccount(UUID accountId, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));

        if (dto.name() != null) {
            account.setName(dto.name());
        }
        if (dto.type() != null) {
            account.setType(dto.type());
        }
        if (dto.balance() != null) {
            account.setBalance(dto.balance());
        }

        return accountRepository.save(account);
    }

    // Regra 4: Inativar Conta (soft delete)
    public Account inactivateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));

        account.setActive(false);
        return accountRepository.save(account);
    }

    // Regra 5: Reativar Conta
    public Account reactivateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));

        account.setActive(true);
        return accountRepository.save(account);
    }
}