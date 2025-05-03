package com.bernardomerlo.ponto_eletronico.login;

import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.RoleEnum;
import com.bernardomerlo.ponto_eletronico.records.LoginRequest;
import com.bernardomerlo.ponto_eletronico.records.LoginResponse;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import com.bernardomerlo.ponto_eletronico.services.JwtService;
import com.bernardomerlo.ponto_eletronico.services.UserService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);

    private final UserService userService = new UserService(userRepository, jwtService);

    @Test
    void deveRetornarTokenEPerfilComCredenciaisValidas() {
        var user = new User("João", "joao@email.com", "123456", RoleEnum.EMPLOYEE);
        var request = new LoginRequest("joao@email.com", "123456");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getId(), user.getEmail(), user.getName())).thenReturn("token_fake");

        LoginResponse response = userService.login(request);

        assertEquals("token_fake", response.token());
        assertEquals(RoleEnum.EMPLOYEE, response.role());
    }

    @Test
    void deveLancarErroComEmailInvalido() {
        var request = new LoginRequest("naoexiste@email.com", "qualquer");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("Email inválido", exception.getMessage());
    }

    @Test
    void deveLancarErroComSenhaInvalida() {
        var user = new User("João", "joao@email.com", "123456", RoleEnum.EMPLOYEE);
        var request = new LoginRequest("joao@email.com", "errada");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("Senha inválida", exception.getMessage());
    }
}
