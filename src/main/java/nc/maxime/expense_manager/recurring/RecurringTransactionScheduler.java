package nc.maxime.expense_manager.recurring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionScheduler {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionScheduler(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void dispatchRecurringTransactions() {
        recurringTransactionService.processDueRecurringTransactions();
    }
}
