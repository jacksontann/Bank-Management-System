package banking.service;

import banking.data.AccountManager;
import banking.data.TransactionManager;
import banking.data.UserManager;
import banking.exception.BankingException;
import banking.model.Account;
import banking.model.Transaction;
import banking.model.TransactionType;
import banking.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


/**
 * MainService handles the business logic for the main banking operations.
 * This class separates the functional operations from the MainWindow View.
 */
public class MainService {

    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    private final User currentUser;
    private Account selectedAccount;


    /**
     * Creates a new MainService instance for the specified user.
     *
     * @param userEmail the email of the user to load
     * @throws SQLException if a database error occurs while loading the user
     */
    public MainService(String userEmail) throws SQLException {
        UserManager userManager = new UserManager();
        this.accountManager = new AccountManager();
        this.transactionManager = new TransactionManager();
        this.currentUser = userManager.loadUser(userEmail);
    }


    /// Gets the current user.
    public User getCurrentUser() {
        return currentUser;
    }


    /// Sets the selected account.
    public void setSelectedAccount(Account account) {
        this.selectedAccount = account;
    }


    /**
     * Validates that an account is selected.
     *
     * @return ValidationResult containing success status and message
     */
    private ValidationResult validateAccountSelected() {
        if (selectedAccount == null)
            return new ValidationResult(false, "No account selected.");
        return new ValidationResult(true, "Account selected.");
    }


    /**
     * Validates and parses an amount string.
     *
     * @param amountText the amount string to validate and parse
     * @return AmountValidationResult containing success status, message, and parsed amount
     */
    private AmountValidationResult validateAndParseAmount(String amountText) {
        if (amountText == null || amountText.trim().isEmpty())
            return new AmountValidationResult(false, "Please enter an amount.", 0.0);

        try {
            double amount = Double.parseDouble(amountText.trim());
            if (amount <= 0)
                return new AmountValidationResult(false, "Amount must be positive.", amount);

            return new AmountValidationResult(true, "Amount is valid.", amount);
        } catch (NumberFormatException ex) {
            return new AmountValidationResult(false, "Invalid amount format.", 0.0);
        }
    }


    /**
     * Validates and parses an account number string.
     *
     * @param accountNumberText the account number string to validate and parse
     * @return AccountNumberValidationResult containing success status, message, and parsed account number
     */
    private AccountNumberValidationResult validateAndParseAccountNumber(String accountNumberText) {
        if (accountNumberText == null || accountNumberText.trim().isEmpty())
            return new AccountNumberValidationResult(false, "Please enter account number.", 0);

        try {
            int accountNumber = Integer.parseInt(accountNumberText.trim());
            return new AccountNumberValidationResult(true, "Account number is valid.", accountNumber);
        } catch (NumberFormatException ex) {
            return new AccountNumberValidationResult(false, "Invalid account number format.", 0);
        }
    }


    /**
     * Validates that the selected account has sufficient balance for a transaction.
     *
     * @param amount the amount to validate against the balance
     * @return ValidationResult containing success status and message
     */
    private ValidationResult validateSufficientBalance(double amount) {
        if (selectedAccount.getBalance() < amount)
            return new ValidationResult(false, "Insufficient balance.");
        return new ValidationResult(true, "Sufficient balance available.");
    }


    /// Gets all accounts for the current user.
    public AccountListResult getUserAccounts() {
        try {
            List<Account> accounts = accountManager.loadAccounts(currentUser.getUserID());
            return new AccountListResult(true, null, accounts);
        } catch (SQLException ex) {
            return new AccountListResult(false, "Failed to load accounts: " + ex.getMessage(), null);
        }
    }


    /**
     * Handles deposit operation.
     *
     * @param amountText the amount to deposit as a string
     * @return TransactionResult indicating success or failure with message
     */
    public TransactionResult handleDeposit(String amountText) {
        ValidationResult accountValidation = validateAccountSelected();
        if (!accountValidation.success())
            return new TransactionResult(false, accountValidation.message());

        AmountValidationResult amountValidation = validateAndParseAmount(amountText);
        if (!amountValidation.success())
            return new TransactionResult(false, amountValidation.message());

        try {
            accountManager.depositMoney(selectedAccount, amountValidation.amount());
            saveTransaction(selectedAccount, selectedAccount, amountValidation.amount(), TransactionType.DEPOSIT.getLabel());
            return new TransactionResult(true, "Deposit successful!");

        } catch (BankingException ex) {
            return new TransactionResult(false, ex.getMessage());
        } catch (SQLException ex) {
            return new TransactionResult(false, "Deposit failed: " + ex.getMessage());
        }
    }


    /**
     * Handles withdrawal operation.
     *
     * @param amountText the amount to withdraw as a string
     * @return TransactionResult indicating success or failure with message
     */
    public TransactionResult handleWithdraw(String amountText) {
        ValidationResult accountValidation = validateAccountSelected();
        if (!accountValidation.success())
            return new TransactionResult(false, accountValidation.message());

        AmountValidationResult amountValidation = validateAndParseAmount(amountText);
        if (!amountValidation.success())
            return new TransactionResult(false, amountValidation.message());

        ValidationResult balanceValidation = validateSufficientBalance(amountValidation.amount());
        if (!balanceValidation.success())
            return new TransactionResult(false, balanceValidation.message());

        try {
            accountManager.withdrawMoney(selectedAccount, amountValidation.amount());
            saveTransaction(selectedAccount, selectedAccount, amountValidation.amount(), TransactionType.WITHDRAWAL.getLabel());
            return new TransactionResult(true, "Withdrawal successful!");

        } catch (BankingException ex) {
            return new TransactionResult(false, ex.getMessage());
        } catch (SQLException ex) {
            return new TransactionResult(false, "Withdrawal failed: " + ex.getMessage());
        }
    }


    /**
     * Handles transfer operation.
     *
     * @param accountNumberText the recipient account number as a string.
     * @param amountText        the amount to transfer as a string.
     * @param comment           optional comment for the transfer.
     * @return a TransactionResult indicating success or failure with message.
     */
    public TransactionResult handleTransfer(String accountNumberText, String amountText, String comment) {
        ValidationResult accountValidation = validateAccountSelected();
        if (!accountValidation.success())
            return new TransactionResult(false, accountValidation.message());

        AccountNumberValidationResult accountNumberValidation = validateAndParseAccountNumber(accountNumberText);
        if (!accountNumberValidation.success())
            return new TransactionResult(false, accountNumberValidation.message());

        AmountValidationResult amountValidation = validateAndParseAmount(amountText);
        if (!amountValidation.success())
            return new TransactionResult(false, amountValidation.message());

        ValidationResult balanceValidation = validateSufficientBalance(amountValidation.amount());
        if (!balanceValidation.success())
            return new TransactionResult(false, balanceValidation.message());

        try {
            Account recipientAccount = accountManager.loadAccount(accountNumberValidation.accountNumber());
            if (recipientAccount == null)
                return new TransactionResult(false, "Recipient account not found.");

            if (recipientAccount.getAccountNumber() == selectedAccount.getAccountNumber())
                return new TransactionResult(false, "Cannot transfer to the same account.");

            accountManager.transferMoney(selectedAccount.getAccountNumber(),
                    accountNumberValidation.accountNumber(),
                    amountValidation.amount());

            String finalComment = (comment != null && !comment.trim().isEmpty())
                    ? comment.trim()
                    : TransactionType.TRANSFER.getLabel();
            saveTransaction(selectedAccount, recipientAccount, amountValidation.amount(), finalComment);

            return new TransactionResult(true, "Transfer successful!");

        } catch (BankingException ex) {
            return new TransactionResult(false, ex.getMessage());
        } catch (SQLException ex) {
            return new TransactionResult(false, "Transfer failed: " + ex.getMessage());
        }
    }


    /// Handles opening a new account
    public AccountResult handleOpenAccount() {
        try {
            Random rand = new Random();
            int accountNumber;
            do {
                accountNumber = rand.nextInt(10000000, 99999999);
            } while (accountManager.accountExists(accountNumber));

            Account newAccount = new Account(0, currentUser.getUserID(), accountNumber, 0.0, false);
            accountManager.saveAccount(newAccount);
            return new AccountResult(true, "Account created successfully! Account number: " + accountNumber);

        } catch (SQLException ex) {
            return new AccountResult(false, "Failed to create account: " + ex.getMessage());
        }
    }


    /// Handles freezing an account
    public AccountResult handleFreezeAccount() {
        if (selectedAccount == null)
            return new AccountResult(false, "No account selected.");

        try {
            accountManager.freezeAccount(selectedAccount);
            return new AccountResult(true, "Account frozen successfully!");

        } catch (SQLException ex) {
            return new AccountResult(false, "Failed to freeze account: " + ex.getMessage());
        }
    }


    /// Handles unfreezing an account
    public AccountResult handleUnfreezeAccount() {
        if (selectedAccount == null)
            return new AccountResult(false, "No account selected.");

        try {
            accountManager.unfreezeAccount(selectedAccount);
            return new AccountResult(true, "Account unfrozen successfully!");

        } catch (SQLException ex) {
            return new AccountResult(false, "Failed to unfreeze account: " + ex.getMessage());
        }
    }


    /// Handles closing an account
    public AccountResult handleCloseAccount() {
        if (selectedAccount == null)
            return new AccountResult(false, "No account selected.");

        try {
            if (selectedAccount.getBalance() > 0)
                return new AccountResult(false, "Cannot close account with positive balance. " +
                        "Please withdraw all funds first.");

            accountManager.deleteAccount(selectedAccount);
            return new AccountResult(true, "Account closed successfully!");

        } catch (SQLException ex) {
            return new AccountResult(false, "Failed to close account: " + ex.getMessage());
        }
    }


    /// Gets transactions for the selected account
    public TransactionListResult getTransactions() {
        if (selectedAccount == null)
            return new TransactionListResult(false, "No account selected.", null);

        try {
            List<Transaction> transactions = transactionManager.loadTransactions(selectedAccount);
            return new TransactionListResult(true, null, transactions);
        } catch (SQLException ex) {
            return new TransactionListResult(false, "Failed to load transactions: " + ex.getMessage(), null);
        }
    }


    /**
     * Saves a transaction record to the database.
     * Throws SQLException so callers are aware when a record could not be persisted.
     *
     * @param sender   the account sending the money
     * @param receiver the account receiving the money
     * @param amount   the amount of money transferred
     * @param comment  a label or user comment for the transaction
     */
    private void saveTransaction(Account sender, Account receiver, double amount, String comment) throws SQLException {
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction(0, sender, receiver, amount, comment, timestamp);
        transactionManager.saveTransaction(transaction);
    }


    /// Result class for transaction operations
    public record TransactionResult(boolean success, String message) { }

    /// Result class for account operations
    public record AccountResult(boolean success, String message) { }

    /// Result class for account list operations
    public record AccountListResult(boolean success, String errorMessage, List<Account> accounts) { }

    /// Result class for transaction list operations
    public record TransactionListResult(boolean success, String errorMessage, List<Transaction> transactions) { }

    /// Result class for validation operations
    public record ValidationResult(boolean success, String message) { }

    /// Result class for amount validation operations
    public record AmountValidationResult(boolean success, String message, double amount) { }

    /// Result class for account number validation operations
    public record AccountNumberValidationResult(boolean success, String message, int accountNumber) { }

}
