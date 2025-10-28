package nc.maxime.expense_manager.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;
import nc.maxime.expense_manager.account.dto.AccountDto;
import nc.maxime.expense_manager.transaction.TransactionType;

public record TransactionDto(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String label,
        String notes,
        String merchant,
        AccountDto account,
        Long categoryId,
        Instant createdAt,
        Instant updatedAt) {
}
