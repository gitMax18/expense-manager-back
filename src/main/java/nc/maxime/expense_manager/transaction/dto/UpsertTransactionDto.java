package nc.maxime.expense_manager.transaction.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.transaction.TransactionType;

public record UpsertTransactionDto(
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
        @NotNull TransactionType type,
        String label,
        String notes,
        String merchant,
        @NotNull Long accountId,
        Long categoryId) {
}
