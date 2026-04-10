package nc.maxime.expense_manager.dashboard.dto;

import org.springframework.stereotype.Component;

import nc.maxime.expense_manager.dashboard.CategoryExpense;

@Component
public class DashboardMapper {

    public CategoryExpenseDto toCategoryExpenseDto(CategoryExpense categoryExpense) {
        return new CategoryExpenseDto(
                categoryExpense.id(),
                categoryExpense.categoryName(),
                categoryExpense.totalAmount(),
                categoryExpense.color());
    }
}
