package nc.maxime.expense_manager.account.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.account.AccountType;

public record CreateAccountDto(
                @NotBlank String name,
                String description,
                @NotNull BigDecimal balance,
                @NotNull AccountType type,
                @NotBlank String currency) {
}
