package banking.model;

import java.time.LocalDateTime;


/**
 * Represents a financial transaction between two accounts.
 * Each transaction has a sender, receiver, amount, comment, and timestamp.
 * Implemented as a record — all accessor methods (transactionID(), sender(), etc.)
 * are generated automatically by the compiler.
 */
public record Transaction(int transactionID, Account sender, Account receiver,
                          double amount, String comment, LocalDateTime date) {

    /**
     * Creates a new Transaction with sender, receiver, amount, comment, and date. Transaction ID is set to 0.
     *
     * @param sender   the sender account
     * @param receiver the receiver account
     * @param amount   the transaction amount
     * @param comment  the transaction comment
     * @param date     the transaction date and time
     */
    public Transaction(Account sender, Account receiver, double amount, String comment, LocalDateTime date) {
        this(0, sender, receiver, amount, comment, date);
    }

}
