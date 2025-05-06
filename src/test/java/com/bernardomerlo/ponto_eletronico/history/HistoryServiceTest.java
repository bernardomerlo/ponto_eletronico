package com.bernardomerlo.ponto_eletronico.history;

import com.bernardomerlo.ponto_eletronico.entities.PunchClock;
import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.records.HistoryResponse;
import com.bernardomerlo.ponto_eletronico.repositories.PunchRepository;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HistoryServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PunchRepository punchRepository = mock(PunchRepository.class);

    private final PunchService punchService = new PunchService(userRepository, punchRepository);

    @Test
    @DisplayName("Deve retornar os dias em que foram batidos os pontos")
    public void deveRetornarHistoricoDePontoPorDia() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<PunchClock> punches = getPunchClocks(user);

        when(punchRepository.findByUserId(userId)).thenReturn(Optional.of(new ArrayList<>(punches)));

        List<HistoryResponse> history = punchService.history(userId);

        assertEquals(1, history.size());
        HistoryResponse h = history.getFirst();

        assertEquals("2025-05-04", h.date());
        assertEquals("08:00", h.checkInHour());
        assertEquals("17:00", h.checkOutHour());
        assertEquals(new BigDecimal("8.00"), h.hoursWorked());
    }

    private static List<PunchClock> getPunchClocks(User user) {
        LocalDate day = LocalDate.of(2025, 5, 4);
        return List.of(
                new PunchClock(user, PunchType.CHECK_IN, LocalDateTime.of(day, LocalTime.of(8, 0))),
                new PunchClock(user, PunchType.CHECK_OUT, LocalDateTime.of(day, LocalTime.of(12, 0))),
                new PunchClock(user, PunchType.CHECK_IN, LocalDateTime.of(day, LocalTime.of(13, 0))),
                new PunchClock(user, PunchType.CHECK_OUT, LocalDateTime.of(day, LocalTime.of(17, 0)))
        );
    }
}