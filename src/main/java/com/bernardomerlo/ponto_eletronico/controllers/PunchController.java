package com.bernardomerlo.ponto_eletronico.controllers;

import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import com.bernardomerlo.ponto_eletronico.records.PunchRequest;
import com.bernardomerlo.ponto_eletronico.records.PunchResponse;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/punch-clock")
public class PunchController {

    @Autowired
    private PunchService punchService;

    @PostMapping
    public ResponseEntity<PunchResponse> punchClock(@RequestBody PunchRequest punchRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        PunchType type = PunchType.valueOf(punchRequest.type().toUpperCase());
        PunchResponse response = punchService.punchClock(userId, type);
        return ResponseEntity.ok(response);
    }

}
