package de.lino.setup.error;

/**
 * Exception class triggered if setup is already finished but one is trying to call a next/new step
 */
public class SetupFinishedException extends RuntimeException {

    /**
     * Constructor for custom exception for generic setup
     * @param message containing error message
     */
    public SetupFinishedException(String message) {
        super(message);
    }

}
