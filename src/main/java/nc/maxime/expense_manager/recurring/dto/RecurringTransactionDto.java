package nc.maxime.expense_manager.recurring.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import nc.maxime.expense_manager.account.dto.AccountDto;
import nc.maxime.expense_manager.transaction.TransactionType;
import nc.maxime.expense_manager.recurring.RecurrenceFrequency;

public record RecurringTransactionDto(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String label,
        String notes,
        String merchant,
        AccountDto account,
        Long categoryId,
        RecurrenceFrequency frequency,
        LocalDate startDate,
        LocalDate endDate,
        Integer dayOfMonth,
        Integer monthOfYear,
        DayOfWeek dayOfWeek,
        LocalTime executionTime,
        LocalDateTime nextExecutionDate,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt) {}
