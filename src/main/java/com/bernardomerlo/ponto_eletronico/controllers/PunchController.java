package com.bernardomerlo.ponto_eletronico.controllers;

import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.records.HistoryResponse;
import com.bernardomerlo.ponto_eletronico.records.PunchRequest;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/punch-clock")
public class PunchController {

    private final PunchService punchService;

    public PunchController(PunchService punchService) {
        this.punchService = punchService;
    }

    @PostMapping
    public ResponseEntity<PunchResponse> punchClock(@AuthenticationPrincipal Long userId, @RequestBody PunchRequest request) {
        PunchType type = PunchType.valueOf(request.type().toUpperCase());
        return ResponseEntity.ok(punchService.punchClock(userId, type));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> history(@AuthenticationPrincipal Long userId) {
        List<HistoryResponse> history = punchService.history(userId);
        return ResponseEntity.ok(history);
    }

}
