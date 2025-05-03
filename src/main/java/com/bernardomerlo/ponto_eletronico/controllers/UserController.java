package com.bernardomerlo.ponto_eletronico.controllers;

import com.bernardomerlo.ponto_eletronico.records.LoginRequest;
import com.bernardomerlo.ponto_eletronico.records.LoginResponse;
import com.bernardomerlo.ponto_eletronico.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginUser){
        LoginResponse loginResponse = this.userService.login(loginUser);
        return ResponseEntity.ok(loginResponse);
    }
}
