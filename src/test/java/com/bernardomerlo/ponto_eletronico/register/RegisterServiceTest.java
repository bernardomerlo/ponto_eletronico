package com.bernardomerlo.ponto_eletronico.register;

import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.RoleEnum;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidEmailException;
import com.bernardomerlo.ponto_eletronico.records.RegisterRequest;
import com.bernardomerlo.ponto_eletronico.records.RegisterResponse;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import com.bernardomerlo.ponto_eletronico.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository, null);

    @Test
    @DisplayName("deve registrar usuario com dados validos")
    void shouldRegisterUserWithValidData() {
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "senha123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        RegisterResponse response = userService.register(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("User created successfully", response.message());
    }

    @Test
    @DisplayName("deve lancar erro ao tentar registrar usuario com dados invalidos")
    void shouldNotRegisterUserWithInvalidData() {
        var user = new User("João", "joao@email.com", "senha123", RoleEnum.EMPLOYEE);
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "senha123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> userService.register(request));
        assertEquals("Esse email já encontra-se cadastrado", exception.getMessage());
    }
}
