package nc.maxime.expense_manager.dashboard;

import java.util.Collection;
import java.util.List;
import nc.maxime.expense_manager.transaction.Transaction;
import nc.maxime.expense_manager.transaction.TransactionType;
import nc.maxime.expense_manager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends JpaRepository<Transaction, Long> {

  @Query("""
      SELECT new nc.maxime.expense_manager.dashboard.CategoryExpense(
          c.id,
          c.name,
          SUM(t.amount),
          c.color)
      FROM Transaction t
      LEFT JOIN t.category c
      WHERE t.type IN :types
        AND t.account.user = :user
      GROUP BY c.id, c.name, c.color
      """)
  List<CategoryExpense> findTotalsByTypeAndCategory(
      @Param("user") User user, @Param("types") Collection<TransactionType> types);
}
