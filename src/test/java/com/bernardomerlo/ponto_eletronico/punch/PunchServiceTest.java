package com.bernardomerlo.ponto_eletronico.punch;

import com.bernardomerlo.ponto_eletronico.entities.PunchClock;
import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidPunchException;
import com.bernardomerlo.ponto_eletronico.exceptions.UnknownUserException;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.repositories.PunchRepository;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PunchServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PunchRepository punchRepository;
    @InjectMocks
    private PunchService punchService;

    @Test
    @DisplayName("deve registrar ponto com sucesso e retornar mensagem + timestamp")
    void punchClockWithSuccess() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(punchRepository.existsTodayByType(userId, PunchType.CHECK_IN)).thenReturn(false);

        PunchResponse response = punchService.punchClock(userId, PunchType.CHECK_IN);

        assertThat(response.message()).isEqualTo("Ponto registrado com sucesso");
        assertThat(response.timestamp()).isNotNull();

        ArgumentCaptor<PunchClock> captor = ArgumentCaptor.forClass(PunchClock.class);
        verify(punchRepository).save(captor.capture());

        PunchClock saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(PunchType.CHECK_IN);
    }

    @Test
    @DisplayName("deve lançar UnknownUserException quando usuário não existir")
    void punchClockWithUnknownUserShouldThrowException() {
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                punchService.punchClock(userId, PunchType.CHECK_OUT))
                .isInstanceOf(UnknownUserException.class)
                .hasMessage("Usuário não encontrado");

        verifyNoInteractions(punchRepository);
    }

    @Test
    @DisplayName("deve lançar InvalidPunchException quando ponto já existe no dia")
    void punchClockWithDuplicateShouldThrowException() {
        long userId = 3L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(punchRepository.existsTodayByType(userId, PunchType.CHECK_IN)).thenReturn(true);

        assertThatThrownBy(() ->
                punchService.punchClock(userId, PunchType.CHECK_IN))
                .isInstanceOf(InvalidPunchException.class)
                .hasMessage("Usuário já realizou este ponto hoje.");

        verify(punchRepository, never()).save(any(PunchClock.class));
    }
}
