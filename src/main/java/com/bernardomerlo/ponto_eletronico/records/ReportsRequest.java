package com.bernardomerlo.ponto_eletronico.records;

import java.time.LocalDateTime;

public record ReportsRequest(LocalDateTime startDate, LocalDateTime endDate) {
}
