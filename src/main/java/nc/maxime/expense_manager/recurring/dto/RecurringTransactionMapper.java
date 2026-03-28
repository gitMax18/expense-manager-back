package nc.maxime.expense_manager.recurring.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.account.dto.AccountMapper;
import nc.maxime.expense_manager.category.TransactionCategory;
import nc.maxime.expense_manager.recurring.RecurringTransaction;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionMapper {

        private final AccountMapper accountMapper;

        public RecurringTransactionMapper(AccountMapper accountMapper) {
                this.accountMapper = accountMapper;
        }

        public RecurringTransaction toEntity(
                        Account account,
                        TransactionCategory category,
                        UpsertRecurringTransactionDto request,
                        LocalDate startDate,
                        LocalDateTime nextExecutionDate,
                        LocalTime executionTime,
                        Integer dayOfMonth,
                        Integer monthOfYear,
                        DayOfWeek dayOfWeek) {
                var payload = Optional.ofNullable(request)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid recurring transaction request"));

                return RecurringTransaction.builder()
                                .amount(payload.amount())
                                .type(payload.type())
                                .label(payload.label())
                                .notes(payload.notes())
                                .merchant(payload.merchant())
                                .account(account)
                                .category(category)
                                .frequency(payload.frequency())
                                .startDate(startDate)
                                .endDate(payload.endDate())
                                .dayOfMonth(dayOfMonth)
                                .monthOfYear(monthOfYear)
                                .dayOfWeek(dayOfWeek)
                                .executionTime(executionTime)
                                .nextExecutionDate(nextExecutionDate)
                                .isActive(true)
                                .build();
        }

        public RecurringTransaction updateEntity(
                        RecurringTransaction recurringTransaction,
                        Account account,
                        TransactionCategory category,
                        UpsertRecurringTransactionDto request,
                        LocalDate startDate,
                        LocalDateTime nextExecutionDate,
                        LocalTime executionTime,
                        Integer dayOfMonth,
                        Integer monthOfYear,
                        DayOfWeek dayOfWeek) {
                var payload = Optional.ofNullable(request)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid recurring transaction update payload"));

                return Optional.ofNullable(recurringTransaction)
                                .map(existing -> {
                                        existing.setAmount(payload.amount());
                                        existing.setType(payload.type());
                                        existing.setLabel(payload.label());
                                        existing.setNotes(payload.notes());
                                        existing.setMerchant(payload.merchant());
                                        existing.setAccount(account);
                                        existing.setCategory(category);
                                        existing.setFrequency(payload.frequency());
                                        existing.setStartDate(startDate);
                                        existing.setEndDate(payload.endDate());
                                        existing.setDayOfMonth(dayOfMonth);
                                        existing.setMonthOfYear(monthOfYear);
                                        existing.setDayOfWeek(dayOfWeek);
                                        existing.setExecutionTime(executionTime);
                                        existing.setNextExecutionDate(nextExecutionDate);
                                        return existing;
                                })
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Recurring transaction entity required"));
        }

        public RecurringTransactionDto toDto(RecurringTransaction recurringTransaction) {
                return new RecurringTransactionDto(
                                recurringTransaction.getId(),
                                recurringTransaction.getAmount(),
                                recurringTransaction.getType(),
                                recurringTransaction.getLabel(),
                                recurringTransaction.getNotes(),
                                recurringTransaction.getMerchant(),
                                Optional.ofNullable(recurringTransaction.getAccount())
                                                .map(accountMapper::toDto)
                                                .orElse(null),
                                Optional.ofNullable(recurringTransaction.getCategory())
                                                .map(TransactionCategory::getId)
                                                .orElse(null),
                                recurringTransaction.getFrequency(),
                                recurringTransaction.getStartDate(),
                                recurringTransaction.getEndDate(),
                                recurringTransaction.getDayOfMonth(),
                                recurringTransaction.getMonthOfYear(),
                                recurringTransaction.getDayOfWeek(),
                                recurringTransaction.getExecutionTime(),
                                recurringTransaction.getNextExecutionDate(),
                                recurringTransaction.isActive(),
                                true,
                                recurringTransaction.getCreatedAt(),
                                recurringTransaction.getUpdatedAt());
        }
}
