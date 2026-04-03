package com.adrielle.corefinancas.integration;

import com.adrielle.corefinancas.dtos.CategoryRequestDTO;
import com.adrielle.corefinancas.entities.User;
import com.adrielle.corefinancas.enums.CategoryType;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TokenService tokenService;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Teste");
        testUser.setEmail("teste@category.com");
        testUser.setPasswordHash(passwordEncoder.encode("senha123"));
        testUser = userRepository.save(testUser);

        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryRequestDTO dto = new CategoryRequestDTO(testUser.getId(), "Alimentação", CategoryType.EXPENSE, "#FF5733", null);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alimentação")))
                .andExpect(jsonPath("$.type", is("EXPENSE")));
    }

    @Test
    void shouldCreateSubcategoryWithParent() throws Exception {
        // Create parent category
        CategoryRequestDTO parentDto = new CategoryRequestDTO(testUser.getId(), "Alimentação", CategoryType.EXPENSE, "#FF5733", null);
        String parentResponse = mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID parentId = UUID.fromString(objectMapper.readTree(parentResponse).get("id").textValue());

        // Create subcategory
        CategoryRequestDTO subDto = new CategoryRequestDTO(testUser.getId(), "Restaurante", CategoryType.EXPENSE, "#FF0000", parentId);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parentId", is(parentId.toString())));
    }

    @Test
    void shouldSoftDeleteCategory() throws Exception {
        CategoryRequestDTO dto = new CategoryRequestDTO(testUser.getId(), "Lazer", CategoryType.EXPENSE, "#0000FF", null);
        String response = mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID categoryId = UUID.fromString(objectMapper.readTree(response).get("id").textValue());

        // Delete (soft delete)
        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // After soft delete, should not appear in user's active categories
        mockMvc.perform(get("/api/categories/user/" + testUser.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void shouldFilterCategoriesByType() throws Exception {
        CategoryRequestDTO incomeDto = new CategoryRequestDTO(testUser.getId(), "Salário", CategoryType.INCOME, "#00FF00", null);
        CategoryRequestDTO expenseDto = new CategoryRequestDTO(testUser.getId(), "Aluguel", CategoryType.EXPENSE, "#FF0000", null);

        mockMvc.perform(post("/api/categories")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeDto)));

        mockMvc.perform(post("/api/categories")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDto)));

        mockMvc.perform(get("/api/categories/user/" + testUser.getId())
                        .header("Authorization", authToken)
                        .param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].type", is("INCOME")));
    }

    @Test
    void shouldReturnBadRequestOnInvalidColorHex() throws Exception {
        CategoryRequestDTO dto = new CategoryRequestDTO(testUser.getId(), "Teste", CategoryType.EXPENSE, "invalidcolor", null);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }
}
