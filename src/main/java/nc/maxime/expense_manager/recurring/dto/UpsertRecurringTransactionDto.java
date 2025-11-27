package nc.maxime.expense_manager.recurring.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import nc.maxime.expense_manager.transaction.TransactionType;
import nc.maxime.expense_manager.recurring.RecurrenceFrequency;

public record UpsertRecurringTransactionDto(
                @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
                @NotNull TransactionType type,
                String label,
                String notes,
                String merchant,
                @NotNull Long accountId,
                Long categoryId,
                @NotNull RecurrenceFrequency frequency,
                LocalDate startDate,
                LocalDate endDate,
                Integer dayOfMonth,
                Integer monthOfYear,
                DayOfWeek dayOfWeek,
                boolean isActive,
                LocalTime executionTime) {
}
