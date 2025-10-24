package nc.maxime.expense_manager.account.dto;

import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(User user, UpsertAccountDto request) {
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

    public AccountDto toDto(Account account) {
        return new AccountDto(
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

    public Account updateEntity(Account account, UpsertAccountDto request) {
        var payload = Optional.ofNullable(request)
                .orElseThrow(() -> new IllegalArgumentException("Requête de mise à jour d'un compte invalide"));

        return Optional.ofNullable(account)
                .map(existing -> {
                    existing.setName(payload.name());
                    existing.setDescription(payload.description());
                    existing.setBalance(payload.balance());
                    existing.setType(payload.type());
                    existing.setCurrency(payload.currency());
                    return existing;
                })
                .orElseThrow(() -> new IllegalArgumentException("Compte cible de la mise à jour invalide"));
    }
}
