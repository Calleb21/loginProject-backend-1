package br.com.login_project.controller;

import br.com.login_project.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar status 200 OK e um token para credenciais válidas")
    void login_ComCredenciaisValidas_DeveRetornarOkEToken() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("camargo12@gmail.com");
        loginRequest.setSenha("Novasenha@123");

        mockMvc.perform(post("/api/usuarios/login") // Faz um POST para o endpoint
                        .contentType(MediaType.APPLICATION_JSON) // Define o tipo de conteúdo como JSON
                        .content(objectMapper.writeValueAsString(loginRequest))) // Adiciona o corpo da requisição
                .andExpect(status().isOk()) // Espera que a resposta seja 200 OK
                .andExpect(jsonPath("$.token").exists()) // Verifica se a resposta JSON contém o campo "token"
                .andExpect(jsonPath("$.nomeCompleto").exists()); // Verifica se contém o campo "nomeCompleto"
    }

    @Test
    @DisplayName("Deve retornar status 401 Unauthorized para senha incorreta")
    void login_ComSenhaInvalida_DeveRetornarUnauthorized() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("camargo12@gmail.com"); // E-mail de um usuário existente
        loginRequest.setSenha("senha-errada-123");   // Senha incorreta

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Espera que a resposta seja 401 Unauthorized
    }
}
