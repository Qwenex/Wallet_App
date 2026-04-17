package org.example.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record Response(UUID id, BigDecimal balance) {
}
