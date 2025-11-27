package nc.maxime.expense_manager.recurring;

import java.time.LocalDateTime;
import java.util.List;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByAccount(Account account);

    List<RecurringTransaction> findByAccountUser(User user);

    List<RecurringTransaction> findByIsActiveTrueAndNextExecutionDateLessThanEqual(LocalDateTime dateTime);
}
