package com.adrielle.corefinancas.integration;

import com.adrielle.corefinancas.dtos.AccountCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.AccountType;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import com.adrielle.corefinancas.repositories.TransactionRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountIntegrationTest {

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

    @BeforeEach
    void setUp() {
                transactionRepository.deleteAllInBatch();
                accountRepository.deleteAllInBatch();
                categoryRepository.deleteAllInBatch();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Teste");
        testUser.setEmail("teste@account.com");
        testUser.setPasswordHash(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        AccountCreateDTO dto = new AccountCreateDTO(testUser.getId(), "Conta Corrente", AccountType.CHECKING, BigDecimal.valueOf(1000));

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Conta Corrente")))
                .andExpect(jsonPath("$.type", is("CHECKING")))
                .andExpect(jsonPath("$.balance", is(1000)));
    }

    @Test
    void shouldReturnBadRequestOnMissingAccountName() throws Exception {
        AccountCreateDTO dto = new AccountCreateDTO(testUser.getId(), "", AccountType.CHECKING, BigDecimal.ZERO);

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    void shouldListUserAccountsWithPagination() throws Exception {
        // Create two accounts
        AccountCreateDTO dto1 = new AccountCreateDTO(testUser.getId(), "Conta 1", AccountType.CHECKING, BigDecimal.ZERO);
        AccountCreateDTO dto2 = new AccountCreateDTO(testUser.getId(), "Conta 2", AccountType.SAVINGS, BigDecimal.ZERO);

        mockMvc.perform(post("/api/accounts")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)));

        mockMvc.perform(post("/api/accounts")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)));

        mockMvc.perform(get("/api/accounts/user/" + testUser.getId())
                        .header("Authorization", authToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    void shouldInactivateAndReactivateAccount() throws Exception {
        AccountCreateDTO dto = new AccountCreateDTO(testUser.getId(), "Conta Poupança", AccountType.SAVINGS, BigDecimal.ZERO);

        String response = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accountId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(patch("/api/accounts/" + accountId + "/inactivate")
                        .header("Authorization", authToken))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/accounts/" + accountId + "/reactivate")
                        .header("Authorization", authToken))
                .andExpect(status().isOk());
    }
}
