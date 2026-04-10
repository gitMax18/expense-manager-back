package nc.maxime.expense_manager.dashboard.dto;

import java.math.BigDecimal;

public record CategoryExpenseDto(Long id, String categoryName, BigDecimal totalAmount, String color) {
}
