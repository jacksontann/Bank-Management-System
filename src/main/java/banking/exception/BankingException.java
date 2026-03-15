package banking.exception;


/**
 * Base exception class for all domain-specific banking errors.
 * Extends RuntimeException to keep exceptions unchecked, allowing
 * them to propagate naturally through the call stack.
 */
public class BankingException extends RuntimeException {

    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }

}
