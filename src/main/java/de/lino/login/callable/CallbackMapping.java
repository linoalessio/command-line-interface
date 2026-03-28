package de.lino.login.callable;

import java.io.Serializable;

/**
 * Interface for calling a generic function to operate on different levels easily
 * <p> Referencing to main(args) using lambda to simplify call <p/>
 * <p> API node: {@code final CallbackMapping mainCallback = Main::main;} </p>
 *
 */
public interface CallbackMapping extends Serializable {

    /**
     * Call body for calling a function with args
     *
     * @param args parameters of passed function
     * @throws Exception called if something went wrong
     */
    void call(final String... args) throws Exception;

}
