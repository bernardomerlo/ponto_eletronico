package com.bernardomerlo.ponto_eletronico.records;

import java.math.BigDecimal;

public record HistoryResponse(int id, String date, String checkInHour, String checkOutHour, BigDecimal hoursWorked) {
}
