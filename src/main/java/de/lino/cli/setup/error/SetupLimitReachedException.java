package de.lino.cli.setup.error;

/**
 * Exception class triggered if setup has already reached its limit but one is trying to call a next/new step
 */
public class SetupLimitReachedException extends RuntimeException {

    /**
     * Constructor for custom exception for generic setup
     * @param message containing error message
     */
    public SetupLimitReachedException(String message) {
        super(message);
    }

}
