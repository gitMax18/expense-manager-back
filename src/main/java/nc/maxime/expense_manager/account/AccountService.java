package nc.maxime.expense_manager.account;

import java.util.List;
import java.util.Optional;
import nc.maxime.expense_manager.account.dto.AccountMapper;
import nc.maxime.expense_manager.account.dto.UpsertAccountDto;
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

    public Account createAccount(User user, UpsertAccountDto request) {
        User owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account owner"));

        return Optional.ofNullable(request)
                .map(dto -> accountMapper.toEntity(owner, dto))
                .map(accountRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account creation request"));
    }

    public List<Account> getUserAccounts(User user) {
        User owner = Optional.ofNullable(user).orElseThrow(() -> new IllegalArgumentException("User not set"));

        return accountRepository.findByUser(owner);
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public Account updateAccount(Long accountId, UpsertAccountDto request) {
        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .map(account -> accountMapper.updateEntity(account, request))
                .map(accountRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for this user"));
    }

    public void deleteAccount(Long accountId) {
        Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .ifPresentOrElse(
                        accountRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("Account not found for this user");
                        });
    }
}
