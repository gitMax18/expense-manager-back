package nc.maxime.expense_manager.dashboard;

import java.math.BigDecimal;

public record CategoryExpense(Long categoryId, String categoryName, BigDecimal totalAmount) {
}
