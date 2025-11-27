package nc.maxime.expense_manager.recurring;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.account.AccountRepository;
import nc.maxime.expense_manager.category.TransactionCategory;
import nc.maxime.expense_manager.category.TransactionCategoryRepository;
import nc.maxime.expense_manager.transaction.TransactionService;
import nc.maxime.expense_manager.transaction.dto.UpsertTransactionDto;
import nc.maxime.expense_manager.recurring.dto.RecurringTransactionMapper;
import nc.maxime.expense_manager.recurring.dto.UpsertRecurringTransactionDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionService transactionService;
    private final RecurringTransactionMapper recurringTransactionMapper;

    public RecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            AccountRepository accountRepository,
            TransactionCategoryRepository transactionCategoryRepository,
            TransactionService transactionService,
            RecurringTransactionMapper recurringTransactionMapper) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionService = transactionService;
        this.recurringTransactionMapper = recurringTransactionMapper;
    }

    @Transactional
    public RecurringTransaction createRecurringTransaction(UpsertRecurringTransactionDto request) {
        return Optional.ofNullable(request)
                .map(dto -> {
                    Account account = resolveAccount(dto.accountId());
                    TransactionCategory category = resolveCategory(dto.categoryId());
                    RecurrenceSettings settings = buildRecurrenceSettings(dto, null);
                    return recurringTransactionMapper.toEntity(
                            account,
                            category,
                            dto,
                            settings.startDate,
                            settings.nextExecutionDate,
                            settings.executionTime,
                            settings.dayOfMonth,
                            settings.monthOfYear,
                            settings.dayOfWeek);
                })
                .map(recurringTransactionRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Invalid recurring transaction request"));
    }

    public List<RecurringTransaction> getRecurringTransactions(User user, Long accountId) {
        var owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to retrieve recurring transactions"));

        return Optional.ofNullable(accountId)
                .map(id -> resolveAccount(owner, id))
                .map(recurringTransactionRepository::findByAccount)
                .orElseGet(() -> recurringTransactionRepository.findByAccountUser(owner));
    }

    public RecurringTransaction getRecurringTransaction(Long id) {
        return Optional.ofNullable(id)
                .flatMap(recurringTransactionRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));
    }

    @Transactional
    public RecurringTransaction updateRecurringTransaction(Long id, UpsertRecurringTransactionDto request) {
        return Optional.ofNullable(id)
                .flatMap(recurringTransactionRepository::findById)
                .map(existing -> {
                    var payload = Optional.ofNullable(request)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid recurring transaction update payload"));
                    Account account = resolveAccount(payload.accountId());
                    TransactionCategory category = resolveCategory(payload.categoryId());
                    RecurrenceSettings settings = buildRecurrenceSettings(payload, existing);

                    return recurringTransactionMapper.updateEntity(
                            existing,
                            account,
                            category,
                            payload,
                            settings.startDate,
                            settings.nextExecutionDate,
                            settings.executionTime,
                            settings.dayOfMonth,
                            settings.monthOfYear,
                            settings.dayOfWeek);
                })
                .map(recurringTransactionRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));
    }

    @Transactional
    public RecurringTransaction pauseRecurringTransaction(Long id) {
        return Optional.ofNullable(id)
                .flatMap(recurringTransactionRepository::findById)
                .map(recurring -> {
                    recurring.setActive(false);
                    return recurringTransactionRepository.save(recurring);
                })
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));
    }

    @Transactional
    public RecurringTransaction resumeRecurringTransaction(Long id) {
        return Optional.ofNullable(id)
                .flatMap(recurringTransactionRepository::findById)
                .map(recurring -> {
                    LocalDateTime nextExecutionDate = resolveNextExecutionDate(
                            recurring.getFrequency(),
                            resolveStartDate(recurring.getStartDate()),
                            recurring.getEndDate(),
                            resolveExecutionTime(recurring.getExecutionTime(), recurring.getExecutionTime()),
                            resolveDayOfMonth(recurring.getDayOfMonth(), recurring.getStartDate(), recurring.getDayOfMonth()),
                            resolveMonthOfYear(recurring.getMonthOfYear(), recurring.getStartDate(), recurring.getMonthOfYear()),
                            resolveDayOfWeek(recurring.getDayOfWeek(), recurring.getStartDate(), recurring.getDayOfWeek()));

                    recurring.setNextExecutionDate(nextExecutionDate);
                    recurring.setActive(true);
                    return recurringTransactionRepository.save(recurring);
                })
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));
    }

    @Transactional
    public void processDueRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        recurringTransactionRepository.findByIsActiveTrueAndNextExecutionDateLessThanEqual(now).forEach(recurring -> {
            LocalDateTime nextExecutionDate = recurring.getNextExecutionDate();

            while (shouldExecute(now, nextExecutionDate, recurring.getEndDate())) {
                createTransactionInstance(recurring);
                nextExecutionDate = calculateNextExecutionDate(recurring, nextExecutionDate);

                if (recurring.getEndDate() != null
                        && nextExecutionDate != null
                        && nextExecutionDate.toLocalDate().isAfter(recurring.getEndDate())) {
                    recurring.setActive(false);
                    break;
                }
            }

            if (recurring.getEndDate() != null
                    && nextExecutionDate != null
                    && nextExecutionDate.toLocalDate().isAfter(recurring.getEndDate())) {
                recurring.setActive(false);
            }

            recurring.setNextExecutionDate(nextExecutionDate);
            recurringTransactionRepository.save(recurring);
        });
    }

    private void createTransactionInstance(RecurringTransaction recurringTransaction) {
        Long accountId = Optional.ofNullable(recurringTransaction.getAccount())
                .map(Account::getId)
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction account missing"));

        Long categoryId = Optional.ofNullable(recurringTransaction.getCategory())
                .map(TransactionCategory::getId)
                .orElse(null);

        var transactionDto = new UpsertTransactionDto(
                recurringTransaction.getAmount(),
                recurringTransaction.getType(),
                recurringTransaction.getLabel(),
                recurringTransaction.getNotes(),
                recurringTransaction.getMerchant(),
                accountId,
                categoryId,
                true);

        transactionService.createTransaction(transactionDto);
    }

    private Account resolveAccount(Long accountId) {
        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for recurring transaction"));
    }

    private Account resolveAccount(User owner, Long accountId) {
        Long ownerId = Optional.ofNullable(owner)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User required to resolve account"));

        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .filter(account -> Optional.ofNullable(account.getUser())
                        .map(User::getId)
                        .filter(ownerId::equals)
                        .isPresent())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for recurring transaction"));
    }

    private TransactionCategory resolveCategory(Long categoryId) {
        return Optional.ofNullable(categoryId)
                .flatMap(transactionCategoryRepository::findById)
                .orElse(null);
    }

    private RecurrenceSettings buildRecurrenceSettings(
            UpsertRecurringTransactionDto request, RecurringTransaction existing) {
        LocalDate startDate = resolveStartDate(request.startDate(), Optional.ofNullable(existing)
                .map(RecurringTransaction::getStartDate)
                .orElse(null));
        LocalDate endDate = Optional.ofNullable(request.endDate())
                .orElseGet(() -> Optional.ofNullable(existing)
                        .map(RecurringTransaction::getEndDate)
                        .orElse(null));
        LocalTime executionTime = resolveExecutionTime(
                request.executionTime(), Optional.ofNullable(existing).map(RecurringTransaction::getExecutionTime).orElse(null));
        Integer dayOfMonth = resolveDayOfMonth(
                request.dayOfMonth(), startDate, Optional.ofNullable(existing).map(RecurringTransaction::getDayOfMonth).orElse(null));
        Integer monthOfYear = resolveMonthOfYear(
                request.monthOfYear(), startDate, Optional.ofNullable(existing).map(RecurringTransaction::getMonthOfYear).orElse(null));
        DayOfWeek dayOfWeek = resolveDayOfWeek(
                request.dayOfWeek(), startDate, Optional.ofNullable(existing).map(RecurringTransaction::getDayOfWeek).orElse(null));

        validateDateRange(startDate, endDate);

        LocalDateTime nextExecutionDate = resolveNextExecutionDate(
                request.frequency(), startDate, endDate, executionTime, dayOfMonth, monthOfYear, dayOfWeek);

        return new RecurrenceSettings(startDate, endDate, executionTime, dayOfMonth, monthOfYear, dayOfWeek, nextExecutionDate);
    }

    private LocalDate resolveStartDate(LocalDate startDate) {
        return Optional.ofNullable(startDate).orElse(LocalDate.now());
    }

    private LocalDate resolveStartDate(LocalDate startDate, LocalDate fallback) {
        return Optional.ofNullable(startDate)
                .or(() -> Optional.ofNullable(fallback))
                .orElse(LocalDate.now());
    }

    private LocalTime resolveExecutionTime(LocalTime executionTime, LocalTime fallback) {
        return Optional.ofNullable(executionTime)
                .or(() -> Optional.ofNullable(fallback))
                .orElse(LocalTime.MIDNIGHT);
    }

    private Integer resolveDayOfMonth(Integer dayOfMonth, LocalDate startDate, Integer fallback) {
        return Optional.ofNullable(dayOfMonth)
                .or(() -> Optional.ofNullable(fallback))
                .orElse(Optional.ofNullable(startDate).map(LocalDate::getDayOfMonth).orElse(1));
    }

    private Integer resolveMonthOfYear(Integer monthOfYear, LocalDate startDate, Integer fallback) {
        return Optional.ofNullable(monthOfYear)
                .or(() -> Optional.ofNullable(fallback))
                .orElse(Optional.ofNullable(startDate).map(LocalDate::getMonthValue).orElse(1));
    }

    private DayOfWeek resolveDayOfWeek(DayOfWeek dayOfWeek, LocalDate startDate, DayOfWeek fallback) {
        return Optional.ofNullable(dayOfWeek)
                .or(() -> Optional.ofNullable(fallback))
                .orElse(Optional.ofNullable(startDate).map(LocalDate::getDayOfWeek).orElse(DayOfWeek.MONDAY));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private LocalDateTime resolveNextExecutionDate(
            RecurrenceFrequency frequency,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime executionTime,
            Integer dayOfMonth,
            Integer monthOfYear,
            DayOfWeek dayOfWeek) {
        RecurrenceFrequency recurrence = Optional.ofNullable(frequency)
                .orElseThrow(() -> new IllegalArgumentException("Recurrence frequency required"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextExecution = firstExecutionCandidate(recurrence, startDate, executionTime, dayOfMonth, monthOfYear, dayOfWeek);

        while (nextExecution.isBefore(now)) {
            nextExecution = calculateNextExecutionDate(recurrence, nextExecution, executionTime, dayOfMonth, monthOfYear, dayOfWeek);
        }

        if (endDate != null && nextExecution.toLocalDate().isAfter(endDate)) {
            throw new IllegalArgumentException("Next execution date exceeds configured end date");
        }

        return nextExecution;
    }

    private LocalDateTime firstExecutionCandidate(
            RecurrenceFrequency frequency,
            LocalDate startDate,
            LocalTime executionTime,
            Integer dayOfMonth,
            Integer monthOfYear,
            DayOfWeek dayOfWeek) {
        LocalDate start = Optional.ofNullable(startDate)
                .orElseThrow(() -> new IllegalArgumentException("Start date required"));
        LocalTime time = Optional.ofNullable(executionTime).orElse(LocalTime.MIDNIGHT);

        return switch (frequency) {
            case DAILY -> start.atTime(time);
            case WEEKLY -> start.with(TemporalAdjusters.nextOrSame(dayOfWeek)).atTime(time);
            case MONTHLY -> clampDay(start.getYear(), start.getMonthValue(), dayOfMonth).atTime(time);
            case YEARLY -> clampDay(start.getYear(), monthOfYear, dayOfMonth).atTime(time);
        };
    }

    private LocalDateTime calculateNextExecutionDate(
            RecurrenceFrequency frequency,
            LocalDateTime from,
            LocalTime executionTime,
            Integer dayOfMonth,
            Integer monthOfYear,
            DayOfWeek dayOfWeek) {
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> {
                var baseDate = from.toLocalDate().plusWeeks(1);
                var nextDate = baseDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
                yield nextDate.atTime(executionTime);
            }
            case MONTHLY -> {
                var nextMonth = from.toLocalDate().withDayOfMonth(1).plusMonths(1);
                yield clampDay(nextMonth.getYear(), nextMonth.getMonthValue(), dayOfMonth).atTime(executionTime);
            }
            case YEARLY -> {
                int nextYear = from.getYear() + 1;
                yield clampDay(nextYear, monthOfYear, dayOfMonth).atTime(executionTime);
            }
        };
    }

    private LocalDate clampDay(int year, int month, int dayOfMonth) {
        int safeMonth = Math.max(1, Math.min(month, 12));
        int safeDayInput = Math.max(1, dayOfMonth);
        var firstOfMonth = LocalDate.of(year, safeMonth, 1);
        int safeDay = Math.min(safeDayInput, firstOfMonth.lengthOfMonth());
        return firstOfMonth.withDayOfMonth(safeDay);
    }

    private boolean shouldExecute(LocalDateTime now, LocalDateTime nextExecutionDate, LocalDate endDate) {
        if (nextExecutionDate == null) {
            return false;
        }

        boolean isDue = !nextExecutionDate.isAfter(now);
        boolean withinEndDate = endDate == null || !nextExecutionDate.toLocalDate().isAfter(endDate);
        return isDue && withinEndDate;
    }

    private LocalDateTime calculateNextExecutionDate(RecurringTransaction recurringTransaction, LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return calculateNextExecutionDate(
                recurringTransaction.getFrequency(),
                from,
                resolveExecutionTime(recurringTransaction.getExecutionTime(), recurringTransaction.getExecutionTime()),
                resolveDayOfMonth(recurringTransaction.getDayOfMonth(), recurringTransaction.getStartDate(), recurringTransaction.getDayOfMonth()),
                resolveMonthOfYear(recurringTransaction.getMonthOfYear(), recurringTransaction.getStartDate(), recurringTransaction.getMonthOfYear()),
                resolveDayOfWeek(recurringTransaction.getDayOfWeek(), recurringTransaction.getStartDate(), recurringTransaction.getDayOfWeek()));
    }

    private record RecurrenceSettings(
            LocalDate startDate,
            LocalDate endDate,
            LocalTime executionTime,
            Integer dayOfMonth,
            Integer monthOfYear,
            DayOfWeek dayOfWeek,
            LocalDateTime nextExecutionDate) {}
}
