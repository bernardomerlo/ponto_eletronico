package com.bernardomerlo.ponto_eletronico.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
