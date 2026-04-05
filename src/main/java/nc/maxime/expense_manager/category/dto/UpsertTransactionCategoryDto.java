package nc.maxime.expense_manager.category.dto;

import jakarta.validation.constraints.NotBlank;

public record UpsertTransactionCategoryDto(
                @NotBlank String name,
                String description,
                String color) {
}
