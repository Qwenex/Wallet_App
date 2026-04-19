package org.example.dto;

import org.example.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletRequest(UUID walletId, OperationType operationType, BigDecimal amount) {
}
