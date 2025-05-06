package com.bernardomerlo.ponto_eletronico.services;

import com.bernardomerlo.ponto_eletronico.entities.PunchClock;
import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidPunchException;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidRequestException;
import com.bernardomerlo.ponto_eletronico.exceptions.UnknownUserException;
import com.bernardomerlo.ponto_eletronico.records.HistoryResponse;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.repositories.PunchRepository;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PunchService {

    private final UserRepository userRepository;
    private final PunchRepository punchRepository;

    public PunchService(UserRepository userRepository, PunchRepository punchRepository) {
        this.userRepository = userRepository;
        this.punchRepository = punchRepository;
    }

    private User findById(Long userId) {
        User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UnknownUserException("Usuário não encontrado");
        }
        return user;
    }

    public PunchResponse punchClock(Long userId, PunchType type) {
        User user = findById(userId);

        boolean lastPunchOpt = punchRepository.existsTodayByType(userId, type);
        if (lastPunchOpt) {
            throw new InvalidPunchException("Usuário já realizou este ponto hoje.");
        }

        PunchClock punchClock = new PunchClock(user, type);
        this.punchRepository.save(punchClock);
        return new PunchResponse("Ponto registrado com sucesso", punchClock.getTimestamp());
    }

    public List<HistoryResponse> history(Long userId) {
        User user = findById(userId);

        List<PunchClock> allPunches = punchRepository.findByUserId(user.getId())
                .orElseThrow(() -> new InvalidRequestException("Usuário sem ponto registrado"))
                .stream()
                .sorted(Comparator.comparing(PunchClock::getTimestamp))
                .toList();
        System.out.println("todos " + allPunches);

        Map<LocalDate, List<PunchClock>> punchesPerDay = allPunches.stream()
                .collect(Collectors.groupingBy(pc -> pc.getTimestamp().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()));
        System.out.println("perday " + punchesPerDay);

        DateTimeFormatter hourFmt = DateTimeFormatter.ofPattern("HH:mm");
        List<HistoryResponse> response = new ArrayList<>();
        int tmpID = 1;
        for (var entry : punchesPerDay.entrySet()) {
            System.out.println("entry " + entry);
            LocalDate day = entry.getKey();
            List<PunchClock> punches = entry.getValue();

            Duration total = Duration.ZERO;
            LocalDateTime openCheckIn = null;

            for (PunchClock p : punches) {
                if (p.getType() == PunchType.CHECK_IN) {
                    openCheckIn = p.getTimestamp();
                } else if (p.getType() == PunchType.CHECK_OUT && openCheckIn != null) {
                    total = total.plus(Duration.between(openCheckIn, p.getTimestamp()));
                    openCheckIn = null;
                }
            }

            LocalTime firstIn = punches.stream()
                    .filter(pc -> pc.getType() == PunchType.CHECK_IN)
                    .findFirst()
                    .map(pc -> pc.getTimestamp().toLocalTime())
                    .orElse(null);

            LocalTime lastOut = punches.stream()
                    .filter(pc -> pc.getType() == PunchType.CHECK_OUT)
                    .reduce((first, second) -> second)
                    .map(pc -> pc.getTimestamp().toLocalTime())
                    .orElse(null);

            response.add(new HistoryResponse(
                    tmpID,
                    day.toString(),
                    firstIn != null ? firstIn.format(hourFmt) : null,
                    lastOut != null ? lastOut.format(hourFmt) : null,
                    BigDecimal.valueOf(total.toMinutes())
                            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
            ));
        }
        return response;
    }

}
