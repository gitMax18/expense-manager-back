package nc.maxime.expense_manager.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    EXPENSE("expense"),
    INCOME("income"),
    TRANSFER_IN("transfer_in"),
    TRANSFER_OUT("transfer_out");

    private String value;

    TransactionType(String value) {
        this.value = value;
    }
}
