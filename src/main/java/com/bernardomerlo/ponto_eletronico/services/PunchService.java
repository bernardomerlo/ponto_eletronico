package com.bernardomerlo.ponto_eletronico.services;

import com.bernardomerlo.ponto_eletronico.entities.PunchClock;
import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidPunchException;
import com.bernardomerlo.ponto_eletronico.exceptions.UnknownUserException;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.records.ReportsRequest;
import com.bernardomerlo.ponto_eletronico.records.ReportsResponse;
import com.bernardomerlo.ponto_eletronico.repositories.PunchRepository;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PunchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PunchRepository punchRepository;

    public PunchResponse punchClock(Long userId, PunchType type) {
        User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UnknownUserException("Usuário não encontrado");
        }

        boolean lastPunchOpt = punchRepository.existsTodayByType(userId, type);
        if (lastPunchOpt) {
            throw new InvalidPunchException("Usuário já realizou este ponto hoje.");
        }

        PunchClock punchClock = new PunchClock(user, type);
        this.punchRepository.save(punchClock);
        return new PunchResponse("Ponto registrado com sucesso", punchClock.getTimestamp());
    }

    public ReportsResponse reports(ReportsRequest reportsRequest) {
        List<PunchClock> punchedClocks = this.punchRepository.findByTimestampBetween(reportsRequest.startDate(), reportsRequest.endDate());
        List<>
        for(PunchClock punchClock : punchedClocks) {

        }
        return null;
    };
}
