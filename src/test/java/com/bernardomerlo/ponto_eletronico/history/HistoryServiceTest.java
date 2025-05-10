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

        assertEquals(4, history.size());
        HistoryResponse h = history.get(0);

        assertEquals("2025-05-04", h.date());
        assertEquals("08:00", h.checkInHour());
        assertEquals("12:00", h.checkOutHour());
        assertEquals("4:00", h.hoursWorked());

        h = history.get(1);
        assertEquals("2025-05-05", h.date());
        assertEquals("13:00", h.checkInHour());
        assertEquals("17:00", h.checkOutHour());
        assertEquals("4:00", h.hoursWorked());

        h = history.get(2);
        assertEquals("2025-05-06", h.date());
        assertEquals("11:43", h.checkInHour());
        assertEquals("12:05", h.checkOutHour());
        assertEquals("0:22", h.hoursWorked());

        h = history.get(3);
        assertEquals("2025-05-07", h.date());
        assertEquals("22:14", h.checkInHour());
        assertEquals("22:27", h.checkOutHour());
        assertEquals("0:13", h.hoursWorked());
    }

    private static List<PunchClock> getPunchClocks(User user) {
        LocalDate dayOne = LocalDate.of(2025, 5, 4);
        LocalDate dayTwo = dayOne.plusDays(1);
        LocalDate dayThree = dayTwo.plusDays(1);
        LocalDate dayFour = dayThree.plusDays(1);
        return List.of(
                new PunchClock(1L, user, PunchType.CHECK_IN, LocalDateTime.of(dayOne, LocalTime.of(8, 0))),
                new PunchClock(2L, user, PunchType.CHECK_OUT, LocalDateTime.of(dayOne, LocalTime.of(12, 0))),
                new PunchClock(3L, user, PunchType.CHECK_IN, LocalDateTime.of(dayTwo, LocalTime.of(13, 0))),
                new PunchClock(4L, user, PunchType.CHECK_OUT, LocalDateTime.of(dayTwo, LocalTime.of(17, 0))),
                new PunchClock(5L, user, PunchType.CHECK_IN, LocalDateTime.of(dayThree, LocalTime.of(11, 43))),
                new PunchClock(6L, user, PunchType.CHECK_OUT, LocalDateTime.of(dayThree, LocalTime.of(12, 5))),
                new PunchClock(7L, user, PunchType.CHECK_IN, LocalDateTime.of(dayFour, LocalTime.of(22, 14, 7))),
                new PunchClock(8L, user, PunchType.CHECK_OUT, LocalDateTime.of(dayFour, LocalTime.of(22, 27, 1)))
        );
    }
}