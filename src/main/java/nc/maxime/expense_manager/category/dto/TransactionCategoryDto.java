package nc.maxime.expense_manager.category.dto;

import java.time.Instant;

public record TransactionCategoryDto(
                Long id,
                String name,
                String description,
                String color,
                Instant createdAt,
                Instant updatedAt) {
}
