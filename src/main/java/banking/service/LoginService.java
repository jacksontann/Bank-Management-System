package banking.service;

import banking.data.UserManager;

import java.sql.SQLException;


/**
 * LoginService handles the business logic for user authentication.
 * This class separates authentication logic from the LoginWindow View.
 * Navigation between windows is handled by the UI layer, not the service layer.
 */
public class LoginService {

    private final UserManager userManager;


    /**
     * Creates a new LoginService instance and initializes the UserManager.
     *
     * @throws SQLException if a database error occurs during initialization
     */
    public LoginService() throws SQLException {
        userManager = new UserManager();
    }


    /**
     * Authenticates a user with the provided email and password.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return AuthenticationResult containing success status and message
     */
    public AuthenticationResult authenticateUser(String email, String password) {
        try {
            if (email == null || email.trim().isEmpty())
                return new AuthenticationResult(false, "Email address cannot be empty.");

            if (password == null || password.trim().isEmpty())
                return new AuthenticationResult(false, "Password cannot be empty.");

            boolean isAuthenticated = userManager.authenticateUser(email, password);

            if (isAuthenticated)
                return new AuthenticationResult(true, "Authentication successful.");
            else
                return new AuthenticationResult(false, "Invalid email or password.");

        } catch (SQLException ex) {
            return new AuthenticationResult(false, "Database error occurred during authentication: " + ex.getMessage());
        }
    }


    /// Result class for authentication operations.
    public record AuthenticationResult(boolean success, String message) { }

}
