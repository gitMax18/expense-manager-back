package nc.maxime.expense_manager.account.dto;

import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(User user, CreateAccountDto request) {
        var owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur requis pour créer un compte"));
        return Account.builder()
                .name(request.name())
                .description(request.description())
                .balance(request.balance())
                .type(request.type())
                .currency(request.currency())
                .user(owner)
                .build();
    }

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getDescription(),
                account.getBalance(),
                account.getType(),
                account.getCurrency(),
                account.isArchived(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }
}
