package nc.maxime.expense_manager.dashboard.dto;

import java.math.BigDecimal;

public record CategoryExpenseDto(Long categoryId, String categoryName, BigDecimal totalAmount) {
}
