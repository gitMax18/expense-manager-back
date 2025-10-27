package nc.maxime.expense_manager.transaction.dto;

import java.util.Optional;
import nc.maxime.expense_manager.account.Account;
import nc.maxime.expense_manager.category.TransactionCategory;
import nc.maxime.expense_manager.transaction.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(Account account, TransactionCategory category, UpsertTransactionDto request) {
        return Optional.ofNullable(request)
                .map(dto -> Transaction.builder()
                        .amount(dto.amount())
                        .type(dto.type())
                        .label(dto.label())
                        .notes(dto.notes())
                        .merchant(dto.merchant())
                        .account(account)
                        .category(category)
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction request"));
    }

    public Transaction updateEntity(
            Transaction transaction,
            Account account,
            TransactionCategory category,
            UpsertTransactionDto request) {
        var payload = Optional.ofNullable(request)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction update payload"));

        return Optional.ofNullable(transaction)
                .map(existing -> {
                    existing.setAmount(payload.amount());
                    existing.setType(payload.type());
                    existing.setLabel(payload.label());
                    existing.setNotes(payload.notes());
                    existing.setMerchant(payload.merchant());
                    existing.setAccount(account);
                    existing.setCategory(category);
                    return existing;
                })
                .orElseThrow(() -> new IllegalArgumentException("Transaction entity required"));
    }

    public TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getLabel(),
                transaction.getNotes(),
                transaction.getMerchant(),
                Optional.ofNullable(transaction.getAccount())
                        .map(Account::getId)
                        .orElse(null),
                Optional.ofNullable(transaction.getCategory())
                        .map(TransactionCategory::getId)
                        .orElse(null),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }
}
