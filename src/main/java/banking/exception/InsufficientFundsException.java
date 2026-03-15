package banking.exception;


/**
 * Thrown when a withdrawal or transfer is attempted with insufficient account balance.
 */
public class InsufficientFundsException extends BankingException {

    public InsufficientFundsException(String message) {
        super(message);
    }

}
