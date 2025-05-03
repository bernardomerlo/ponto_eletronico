package com.bernardomerlo.ponto_eletronico.services;

import com.bernardomerlo.ponto_eletronico.entities.User;
import com.bernardomerlo.ponto_eletronico.enums.RoleEnum;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidEmailException;
import com.bernardomerlo.ponto_eletronico.exceptions.InvalidPasswordException;
import com.bernardomerlo.ponto_eletronico.records.LoginRequest;
import com.bernardomerlo.ponto_eletronico.records.LoginResponse;
import com.bernardomerlo.ponto_eletronico.records.RegisterRequest;
import com.bernardomerlo.ponto_eletronico.records.RegisterResponse;
import com.bernardomerlo.ponto_eletronico.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    
    public LoginResponse login(LoginRequest loginUser) {
        Optional<User> userExists = this.userRepository.findByEmail(loginUser.email());
        if (userExists.isEmpty()) {
            throw new InvalidEmailException("Email inválido");
        }
        User user = userExists.get();
        if (!user.getPassword().equals(loginUser.password())) {
            throw new InvalidPasswordException("Senha inválida");
        }
        String jwtToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getName());
        return new LoginResponse(jwtToken, user.getRole());
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<User> emailExists = this.userRepository.findByEmail(registerRequest.email());
        if (emailExists.isPresent()) {
            throw new InvalidEmailException("Esse email já encontra-se cadastrado");
        }
        User user = new User(registerRequest.name(), registerRequest.email(), registerRequest.password(), RoleEnum.EMPLOYEE);
        this.userRepository.save(user);
        return new RegisterResponse(user.getId(), "User created successfully");
    }
}
