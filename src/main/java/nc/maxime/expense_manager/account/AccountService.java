package nc.maxime.expense_manager.account;

import java.util.Optional;
import nc.maxime.expense_manager.account.dto.AccountMapper;
import nc.maxime.expense_manager.account.dto.AccountResponse;
import nc.maxime.expense_manager.account.dto.CreateAccountDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public AccountResponse createAccount(User user, CreateAccountDto request) {
        User owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur propriétaire du compte invalide"));

        return Optional.ofNullable(request)
                .map(dto -> accountMapper.toEntity(owner, dto))
                .map(accountRepository::save)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Requête de création d'un compte invalide"));
    }
}
