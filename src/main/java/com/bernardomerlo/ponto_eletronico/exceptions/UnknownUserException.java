package com.bernardomerlo.ponto_eletronico.exceptions;

import org.springframework.http.HttpStatus;

public class UnknownUserException extends BusinessException {
    public UnknownUserException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
