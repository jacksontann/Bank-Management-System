package banking.model;


/**
 * Represents the type of a financial transaction.
 * Using an enum instead of plain strings prevents typos and
 * makes transaction categorisation explicit and type-safe.
 */
public enum TransactionType {

    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    /// Returns the human-readable label for this transaction type.
    public String getLabel() {
        return label;
    }

}
