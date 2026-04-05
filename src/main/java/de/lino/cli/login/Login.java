package de.lino.cli.login;

import de.lino.cli.database.config.Credentials;
import de.lino.cli.login.callable.CallbackMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * CLI login panel for database connection
 *
 * <p>Check if entered credentials match with config
 * <p>If credentials matches, login granted, denied otherwise
 */
public class Login {

    /**
     * Counting login attempts
     */
    private static int ATTEMPT_COUNT = 1;

    /**
     * Credentials to check for login
     */
    private final String username, password;

    /**
     * Attempt limit, if passed, login denied
     */
    private final int attempts;

    /**
     * Main function to be called of authentification failed for recursive call
     */
    private final CallbackMapping mainFunction;

    /**
     * Class constructor called for initializing login class
     * @param mainFunction {@link de.lino.cli.login.callable.CallbackMapping} Function called of login attempt failed
     * @param attempts limit of attempts
     * @param command CLI input command to extract username and password {@code {userName} {password}}
     */
    public Login(final CallbackMapping mainFunction, final int attempts, final String command) {

        final List<String> data = Arrays.stream(command.split(" ")).toList();

        this.mainFunction = Objects.requireNonNull(mainFunction, "@Login: mainFunction cannot be null");

        this.attempts = attempts;
        this.username = data.size() == 2 ? data.get(0) : "";
        this.password = data.size() == 2 ? data.get(1) : "";

    }

    /**
     * Attempt to authenticate with credentials checking if they match
     *
     * @param credentials Provided credentials for database from file
     * @param succeeded Function called if attempt was successful
     * @param cancelled Function called if attempt finally failed due to too many attempts
     * @param args Main args for recursive callback
     *
     * @param failed Function called if attempt failed
     */
    public void attemptAuthentification(Credentials credentials, Runnable succeeded, Runnable failed, Runnable cancelled, String... args) {

        if (ATTEMPT_COUNT == this.attempts) {
            cancelled.run();
            return;
        }

        if (this.username.equals(credentials.getUsername()) && this.password.equals(credentials.getPassword())) {
            succeeded.run();
            return;
        }

        ATTEMPT_COUNT++;
        failed.run();

        try {
            this.mainFunction.call(args);
        } catch (final Exception exception) {
            throw new RuntimeException(String.format("@attemptAuthentification: Calling main function failed: %s", exception.getMessage()));
        }

    }

    /**
     * Get current attempts for login
     * @return Current attempts
     */
    public int getCurrentAttempt() {
        return ATTEMPT_COUNT;
    }

    /**
     * Return username for login from file
     * @return String
     */
	public String getUsername() {
		return username;
	}

}
