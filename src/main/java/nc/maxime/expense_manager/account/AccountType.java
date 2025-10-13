package nc.maxime.expense_manager.account;

public enum AccountType {
    CHECKING("checking"),
    SAVINGS("savings"),
    INVESTMENT("investment");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
