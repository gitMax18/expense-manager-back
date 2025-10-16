package nc.maxime.expense_manager.account.dto;

import java.math.BigDecimal;
import java.time.Instant;

import nc.maxime.expense_manager.account.AccountType;

public record AccountResponse(
        Long id,
        String name,
        String description,
        BigDecimal balance,
        AccountType type,
        String currency,
        boolean archived,
        Instant createdAt,
        Instant updatedAt) {
}
