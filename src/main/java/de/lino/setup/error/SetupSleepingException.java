package de.lino.setup.error;

/**
 * Exception class triggered if setup is still in 'Start' mode but one is trying to call a next or finish a setup
 */
public class SetupSleepingException extends RuntimeException {

    /**
     * Constructor for custom exception for generic setup
     * @param message containing error message
     */
    public SetupSleepingException(String message) {
        super(message);
    }

}
