package nc.maxime.expense_manager.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nc.maxime.expense_manager.transaction.TransactionType;
import nc.maxime.expense_manager.user.User;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public List<CategoryExpense> getExpenseTotalsByCategory(User user) {
        var owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to build dashboard"));

        return dashboardRepository
                .findTotalsByTypeAndCategory(owner, List.of(TransactionType.EXPENSE, TransactionType.TRANSFER_OUT))
                .stream()
                .map(categoryExpense -> new CategoryExpense(
                        categoryExpense.id(),
                        Optional.ofNullable(categoryExpense.categoryName()).orElse("Uncategorized"),
                        Optional.ofNullable(categoryExpense.totalAmount()).orElse(BigDecimal.ZERO),
                        Optional.ofNullable(categoryExpense.color()).orElse("#000000")))
                .toList();
    }
}
