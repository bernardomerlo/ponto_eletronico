package com.bernardomerlo.ponto_eletronico.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends BusinessException  {
    public InvalidEmailException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
