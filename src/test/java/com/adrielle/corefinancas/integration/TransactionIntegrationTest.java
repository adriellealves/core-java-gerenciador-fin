package com.adrielle.corefinancas.integration;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.dtos.TransactionRequestDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.AccountType;
import com.adrielle.corefinancas.enums.CategoryType;
import com.adrielle.corefinancas.enums.TransactionStatus;
import com.adrielle.corefinancas.enums.TransactionType;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.TransactionRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import com.adrielle.corefinancas.security.TokenService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private User testUser;
    private String authToken;
    private UUID accountId;
    private UUID categoryId;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Teste");
        testUser.setEmail("teste@transaction.com");
        testUser.setPasswordHash(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = "Bearer " + tokenService.generateToken(testUser);

        // Create an account with balance
        AccountCreateDTO accountDto = new AccountCreateDTO(testUser.getId(), "Conta Principal", AccountType.CHECKING, BigDecimal.valueOf(500));
        String accountResponse = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        accountId = UUID.fromString(objectMapper.readTree(accountResponse).get("id").asText());

        // Create a category
        CategoryRequestDTO categoryDto = new CategoryRequestDTO(testUser.getId(), "Salário", CategoryType.INCOME, "#00FF00", null);
        String categoryResponse = mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        categoryId = UUID.fromString(objectMapper.readTree(categoryResponse).get("id").asText());
    }

    @Test
    void shouldCreateIncomeTransactionAndUpdateBalance() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.INCOME, BigDecimal.valueOf(200),
                "Salário recebido", LocalDate.now(), TransactionStatus.PAID, null);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("INCOME")))
                .andExpect(jsonPath("$.amount", is(200)));
    }

    @Test
    void shouldCreateExpenseTransactionAndUpdateBalance() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.EXPENSE, BigDecimal.valueOf(100),
                "Supermercado", LocalDate.now(), TransactionStatus.PAID, null);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("EXPENSE")));
    }

    @Test
    void shouldRejectExpenseWhenInsufficientBalance() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.EXPENSE, BigDecimal.valueOf(9999),
                "Compra cara demais", LocalDate.now(), TransactionStatus.PAID, null);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message", containsString("Saldo insuficiente")));
    }

    @Test
    void shouldRejectTransferWithoutReferenceId() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.TRANSFER, BigDecimal.valueOf(50),
                "Transferência sem referência", LocalDate.now(), TransactionStatus.PENDING, null);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message", containsString("referenceId")));
    }

    @Test
    void shouldListUserTransactionsWithPagination() throws Exception {
        // Create a transaction first
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.INCOME, BigDecimal.valueOf(100),
                "Receita teste", LocalDate.now(), TransactionStatus.PENDING, null);

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/api/transactions/user/" + testUser.getId())
                        .header("Authorization", authToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldFilterTransactionsByType() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.INCOME, BigDecimal.valueOf(50),
                "Receita filtrada", LocalDate.now(), TransactionStatus.PENDING, null);

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/api/transactions/user/" + testUser.getId())
                        .header("Authorization", authToken)
                        .param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].type", everyItem(is("INCOME"))));
    }

    @Test
    void shouldRejectTransactionWithNegativeAmount() throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                testUser.getId(), accountId, categoryId,
                TransactionType.INCOME, BigDecimal.valueOf(-50),
                "Valor inválido", LocalDate.now(), TransactionStatus.PENDING, null);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
