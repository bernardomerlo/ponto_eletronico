package com.bernardomerlo.ponto_eletronico.punch;

import com.bernardomerlo.ponto_eletronico.controllers.PunchController;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidPunchException;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PunchController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class PunchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PunchService punchService;

    private static final Long USER_ID = 1L;
    private static final String ENDPOINT = "/api/v1/punch-clock";

    @BeforeEach
    void setUpSecurityContext() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_ID,
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("deve registrar ponto e devolver 200 + corpo JSON")
    void punchClockWithSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        var expectedResponse = new PunchResponse("Ponto registrado com sucesso", now);

        when(punchService.punchClock(USER_ID, PunchType.CHECK_IN)).thenReturn(expectedResponse);

        String body = """
                {
                  "type": "check_in"
                }
                """;

        mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Ponto registrado com sucesso")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(punchService)
                .punchClock(USER_ID, PunchType.CHECK_IN);
    }

    @Test
    @DisplayName("deve retornar 409 quando já existe ponto do mesmo tipo no dia")
    void punchClockDuplicateReturns409() throws Exception {
        when(punchService.punchClock(USER_ID, PunchType.CHECK_IN))
                .thenThrow(new InvalidPunchException("Usuário já realizou este ponto hoje."));

        String body = """
                {
                  "type": "check_in"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Usuário já realizou este ponto hoje."));
    }
}
