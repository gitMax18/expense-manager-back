package nc.maxime.expense_manager.dashboard.dto;

import nc.maxime.expense_manager.dashboard.CategoryExpense;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {

    public CategoryExpenseDto toCategoryExpenseDto(CategoryExpense categoryExpense) {
        return new CategoryExpenseDto(
                categoryExpense.categoryId(),
                categoryExpense.categoryName(),
                categoryExpense.totalAmount());
    }
}
