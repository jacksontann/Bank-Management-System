package banking.model;

import banking.exception.AccountFrozenException;
import banking.exception.InsufficientFundsException;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a bank account in the banking system.
 * Each account has an ID, belongs to a user, has a unique account number, balance, and can be frozen.
 */
public class Account {

    private final int accountID;
    private final int userID;
    private final int accountNumber;
    private double balance;
    private boolean isFrozen;
    private List<Transaction> transactions;


    /**
     * Creates a new Account with specified account ID, user ID, account number, balance, and frozen status.
     *
     * @param accountID     the account ID
     * @param userID        the user ID who owns this account
     * @param accountNumber the account number
     * @param balance       the initial balance
     * @param frozen        the frozen status
     */
    public Account(int accountID, int userID, int accountNumber, double balance, boolean frozen) {
        this.accountID = accountID;
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.isFrozen = frozen;
        this.transactions = new ArrayList<>();
    }


    /**
     * Creates a new Account with user ID, account number, balance, and frozen status. Account ID is set to 0.
     *
     * @param userID        the user ID who owns this account
     * @param accountNumber the account number
     * @param balance       the initial balance
     * @param frozen        the frozen status
     */
    public Account(int userID, int accountNumber, double balance, boolean frozen) {
        this(0, userID, accountNumber, balance, frozen);
    }


    /// Gets the account ID.
    public int getAccountID() {
        return accountID;
    }

    /// Gets the user ID who owns this account.
    public int getUserID() {
        return userID;
    }

    /// Gets the account number.
    public int getAccountNumber() {
        return accountNumber;
    }

    /// Gets the current balance of the account.
    public double getBalance() {
        return balance;
    }

    /// Checks if the account is frozen.
    public boolean isFrozen() {
        return isFrozen;
    }

    /// Gets a copy of the account's transaction list.
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /// Sets the transaction list for this account.
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /// Freezes the account, preventing transactions.
    public void freeze() {
        isFrozen = true;
    }

    /// Unfreezes the account, allowing transactions.
    public void unfreeze() {
        isFrozen = false;
    }


    /**
     * Deposits the specified amount into this account.
     *
     * @param amount the amount to deposit
     * @throws AccountFrozenException   if the account is frozen
     * @throws IllegalArgumentException if the amount is not positive
     */
    public void deposit(double amount) {
        if (isFrozen)
            throw new AccountFrozenException("Cannot deposit to a frozen account.");

        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive.");

        balance += amount;
    }


    /**
     * Withdraws the specified amount from this account.
     *
     * @param amount the amount to withdraw
     * @throws AccountFrozenException      if the account is frozen
     * @throws InsufficientFundsException  if the amount exceeds the available balance
     * @throws IllegalArgumentException    if the amount is not positive
     */
    public void withdraw(double amount) {
        if (isFrozen)
            throw new AccountFrozenException("Cannot withdraw from a frozen account.");

        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");

        if (amount > balance)
            throw new InsufficientFundsException("Insufficient funds.");

        balance -= amount;
    }

}
