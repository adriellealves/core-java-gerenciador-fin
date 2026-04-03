package com.adrielle.corefinancas.services;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.dtos.AccountUpdateDTO;
import com.adrielle.corefinancas.dtos.PagedResponseDTO;
import com.adrielle.corefinancas.dtos.AccountResponseDTO;
import com.adrielle.corefinancas.entities.Account;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.exceptions.ResourceNotFoundException;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    // Regra 1: Criar Conta
    @Transactional
    public Account createAccount(AccountCreateDTO dto) {
        // Vai no banco confirmar se o utilizador existe. Se não existir, trava o processo.
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setName(dto.name());
        newAccount.setType(dto.type());
        newAccount.setActive(true);
        
        // Se o front-end não mandar saldo, começa com zero por padrão
        newAccount.setBalance(dto.balance() != null ? dto.balance() : BigDecimal.ZERO);

        Account saved = accountRepository.save(newAccount);
        log.info("Conta criada: id={}, userId={}, nome={}", saved.getId(), dto.userId(), dto.name());
        return saved;
    }

    // Regra 2: Listar as Contas de um Utilizador (com paginação)
    @Transactional(readOnly = true)
    public PagedResponseDTO<AccountResponseDTO> findAccountsByUser(UUID userId, Pageable pageable) {
        return PagedResponseDTO.from(
                accountRepository.findByUserIdAndActiveTrue(userId, pageable),
                AccountResponseDTO::new
        );
    }

    // Regra 3: Editar Conta
    @Transactional
    public Account updateAccount(UUID accountId, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada!"));

        if (dto.name() != null) {
            account.setName(dto.name());
        }
        if (dto.type() != null) {
            account.setType(dto.type());
        }
        if (dto.balance() != null) {
            account.setBalance(dto.balance());
        }

        log.info("Conta atualizada: id={}", accountId);
        return accountRepository.save(account);
    }

    // Regra 4: Inativar Conta (soft delete)
    @Transactional
    public Account inactivateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada!"));

        account.setActive(false);
        log.info("Conta inativada: id={}", accountId);
        return accountRepository.save(account);
    }

    // Regra 5: Reativar Conta
    @Transactional
    public Account reactivateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada!"));

        account.setActive(true);
        log.info("Conta reativada: id={}", accountId);
        return accountRepository.save(account);
    }
}