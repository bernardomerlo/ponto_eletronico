package com.bernardomerlo.ponto_eletronico.controllers;

import com.bernardomerlo.ponto_eletronico.records.ReportsRequest;
import com.bernardomerlo.ponto_eletronico.records.ReportsResponse;
import com.bernardomerlo.ponto_eletronico.services.PunchService;
import com.bernardomerlo.ponto_eletronico.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final PunchService punchService;

    public AdminController(PunchService punchService){
        this.punchService = punchService;
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportsResponse> reports(@RequestParam String startDate, @RequestParam String endDate){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);

        ReportsResponse response = this.punchService.reports(new ReportsRequest(start, end));
        return ResponseEntity.ok(response);
    }

}
