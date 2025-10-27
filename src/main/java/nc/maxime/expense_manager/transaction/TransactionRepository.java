package nc.maxime.expense_manager.transaction;

import java.util.List;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountUser(User user);
}
