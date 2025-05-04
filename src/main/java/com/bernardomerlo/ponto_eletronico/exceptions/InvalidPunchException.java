package com.bernardomerlo.ponto_eletronico.exceptions;


import org.springframework.http.HttpStatus;

public class InvalidPunchException extends BusinessException {
    public InvalidPunchException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
