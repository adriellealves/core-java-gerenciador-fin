package com.adrielle.corefinancas.integration;

import com.adrielle.corefinancas.dtos.LoginRequestDTO;
import com.adrielle.corefinancas.dtos.UserCreateDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.repositories.AccountRepository;
import com.adrielle.corefinancas.repositories.CategoryRepository;
import com.adrielle.corefinancas.repositories.TransactionRepository;
import com.adrielle.corefinancas.repositories.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

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

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserAndReturnCreated() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("João Silva", "joao@test.com", "senha123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("joao@test.com")))
                .andExpect(jsonPath("$.name", is("João Silva")));
    }

    @Test
    void shouldReturnConflictOnDuplicateEmail() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("João Silva", "joao@test.com", "senha123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestOnInvalidUserData() throws Exception {
        UserCreateDTO invalidDto = new UserCreateDTO("", "not-an-email", "123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    void shouldLoginAndReturnToken() throws Exception {
        // First create the user
        User user = new User();
        user.setName("Maria");
        user.setEmail("maria@test.com");
        user.setPasswordHash(passwordEncoder.encode("senha123"));
        userRepository.save(user);

        LoginRequestDTO loginDto = new LoginRequestDTO("maria@test.com", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.name", is("Maria")));
    }

    @Test
    void shouldReturnUnauthorizedOnWrongPassword() throws Exception {
        User user = new User();
        user.setName("Maria");
        user.setEmail("maria2@test.com");
        user.setPasswordHash(passwordEncoder.encode("senha123"));
        userRepository.save(user);

        LoginRequestDTO loginDto = new LoginRequestDTO("maria2@test.com", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
