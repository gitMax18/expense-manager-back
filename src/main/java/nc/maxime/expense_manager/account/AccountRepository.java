package nc.maxime.expense_manager.account;

import java.util.List;
import nc.maxime.expense_manager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser(User user);

    List<Account> findByUserId(Long userId);

    List<Account> findByType(AccountType type);
}
