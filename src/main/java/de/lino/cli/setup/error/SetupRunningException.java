package de.lino.cli.setup.error;

/**
 * Exception class triggered if setup is already running but one is trying to introduce it again
 */
public class SetupRunningException extends RuntimeException {

    /**
     * Constructor for custom exception for generic setup
     * @param message containing error message
     */
    public SetupRunningException(String message) {
        super(message);
    }

}
