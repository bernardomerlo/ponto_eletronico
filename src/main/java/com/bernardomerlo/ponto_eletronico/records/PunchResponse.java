package com.bernardomerlo.ponto_eletronico.records;

import java.time.LocalDateTime;

public record PunchResponse(String message, LocalDateTime timestamp) {
}
