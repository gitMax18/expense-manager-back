package nc.maxime.expense_manager.category.dto;

import java.util.Optional;
import nc.maxime.expense_manager.category.TransactionCategory;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionCategoryMapper {

        public TransactionCategory toEntity(User user, UpsertTransactionCategoryDto request) {
                var owner = Optional.ofNullable(user)
                                .orElseThrow(() -> new IllegalArgumentException("User required to create category"));

                return Optional.ofNullable(request)
                                .map(dto -> TransactionCategory.builder()
                                                .name(dto.name())
                                                .description(dto.description())
                                                .color(dto.color())
                                                .user(owner)
                                                .build())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid category payload"));
        }

        public TransactionCategory updateEntity(TransactionCategory category, UpsertTransactionCategoryDto request) {
                var payload = Optional.ofNullable(request)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid category payload"));

                return Optional.ofNullable(category)
                                .map(existing -> {
                                        existing.setName(payload.name());
                                        existing.setDescription(payload.description());
                                        existing.setColor(payload.color());
                                        return existing;
                                })
                                .orElseThrow(() -> new IllegalArgumentException("Category entity required"));
        }

        public TransactionCategoryDto toDto(TransactionCategory category) {
                return new TransactionCategoryDto(
                                category.getId(),
                                category.getName(),
                                category.getDescription(),
                                category.getColor(),
                                category.getCreatedAt(),
                                category.getUpdatedAt());
        }
}
