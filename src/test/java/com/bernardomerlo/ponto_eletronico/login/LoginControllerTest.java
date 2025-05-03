package com.bernardomerlo.ponto_eletronico.login;

import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.RoleEnum;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(new User("Bernardo", "bernardomerlo49@gmail.com", "12345", RoleEnum.EMPLOYEE));
    }

    @Test
    void deveAutenticarUsuarioComDadosValidos() throws Exception {
        String json = """
                {
                    "email": "bernardomerlo49@gmail.com",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void deveRecusarLoginComEmailInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "email": "email-invalido",
                                        "password": "senha123"
                                    }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email inválido"));
    }
}
