package org.example.dto;

import org.example.service.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public record Request(UUID id, OperationType type, BigDecimal amount) {
}
