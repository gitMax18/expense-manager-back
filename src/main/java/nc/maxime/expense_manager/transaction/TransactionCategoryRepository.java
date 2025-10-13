package nc.maxime.expense_manager.transaction;

import java.util.List;
import nc.maxime.expense_manager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {

    List<TransactionCategory> findByUser(User user);

    List<TransactionCategory> findByUserId(Long userId);
}
