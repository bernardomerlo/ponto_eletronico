package com.bernardomerlo.ponto_eletronico.records;

import com.bernardomerlo.ponto_eletronico.enums.RoleEnum;

public record LoginResponse(String token, RoleEnum role) {
}
