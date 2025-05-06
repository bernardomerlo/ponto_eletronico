package com.bernardomerlo.ponto_eletronico.records;

import java.math.BigDecimal;
import java.util.List;

public record ReportsResponse(BigDecimal totalHours, List<ResponseEmployee> employees) {
}
