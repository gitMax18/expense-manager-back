package nc.maxime.expense_manager.dashboard;

import java.math.BigDecimal;

public record CategoryExpense(Long id, String categoryName, BigDecimal totalAmount, String color) {
}
