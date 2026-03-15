package banking.exception;


/**
 * Thrown when an operation is attempted on a frozen account.
 */
public class AccountFrozenException extends BankingException {

    public AccountFrozenException(String message) {
        super(message);
    }

}
