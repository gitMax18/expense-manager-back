package nc.maxime.expense_manager.transaction;

import java.util.List;
import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.account.AccountRepository;
import nc.maxime.expense_manager.category.TransactionCategory;
import nc.maxime.expense_manager.category.TransactionCategoryRepository;
import nc.maxime.expense_manager.transaction.dto.TransactionMapper;
import nc.maxime.expense_manager.transaction.dto.UpsertTransactionDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            TransactionCategoryRepository transactionCategoryRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionMapper = transactionMapper;
    }

    public Transaction createTransaction(UpsertTransactionDto request) {
        return Optional.ofNullable(request)
                .map(dto -> {
                    Account account = resolveAccount(dto.accountId());
                    TransactionCategory category = resolveCategory(dto.categoryId());
                    return transactionMapper.toEntity(account, category, dto);
                })
                .map(transactionRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction creation request"));
    }

    public List<Transaction> getTransactions(User user, Long accountId) {
        var owner = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User required to retrieve transactions"));

        return Optional.ofNullable(accountId)
                .map(id -> resolveAccount(owner, id))
                .map(transactionRepository::findByAccount)
                .orElseGet(() -> transactionRepository.findByAccountUser(owner));
    }

    public Transaction getTransaction(Long transactionId) {
        return Optional.ofNullable(transactionId)
                .flatMap(transactionRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    public Transaction updateTransaction(Long transactionId, UpsertTransactionDto request) {
        return Optional.ofNullable(transactionId)
                .flatMap(transactionRepository::findById)
                .map(existing -> {
                    var payload = Optional.ofNullable(request)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid transaction update payload"));
                    Account account = resolveAccount(payload.accountId());
                    TransactionCategory category = resolveCategory(payload.categoryId());
                    return transactionMapper.updateEntity(existing, account, category, payload);
                })
                .map(transactionRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    public void deleteTransaction(Long transactionId) {
        Optional.ofNullable(transactionId)
                .flatMap(transactionRepository::findById)
                .ifPresentOrElse(
                        transactionRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("Transaction not found");
                        });
    }

    private Account resolveAccount(Long accountId) {
        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for transaction"));
    }

    private Account resolveAccount(User owner, Long accountId) {
        Long ownerId = Optional.ofNullable(owner)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User required to resolve account"));

        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .filter(account -> Optional.ofNullable(account.getUser())
                        .map(User::getId)
                        .filter(ownerId::equals)
                        .isPresent())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for transaction"));
    }

    private TransactionCategory resolveCategory(Long categoryId) {
        return Optional.ofNullable(categoryId)
                .flatMap(transactionCategoryRepository::findById)
                .orElse(null);
    }
}
