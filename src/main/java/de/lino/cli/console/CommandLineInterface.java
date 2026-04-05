package de.lino.cli.console;

import com.google.common.collect.Maps;
import de.lino.cli.entity.Entity;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Handling data for CommandLineInterface
 *
 * <p>Fetching data from {@code java.util.Scanner}
 * <p>Automatically converting arguments to valid data type
 * <p>Handling entered commands for validation check
 */
public class CommandLineInterface implements Entity {

    /**
     * Map containing arguments at index
     */
    private final Map<Integer, String> mappedArguments;

    /**
     * {@code AtomicReference} used for thread-safe method of starting and stopping CLI loop
     */
    private final AtomicBoolean running;

    /**
     * Command line input extracted from CLI for reading current input
     */
    private final Scanner COMMAND_LINE_INPUT;

    /**
     * Constructor for initializing CLI
     * @param commandLineInput ({@link java.util.Scanner}) used scanner for reading input
     */
    public CommandLineInterface(final Scanner commandLineInput) {
        this.COMMAND_LINE_INPUT = commandLineInput;
        this.mappedArguments = Maps.newConcurrentMap();
        this.running = new AtomicBoolean(false);
    }

    /**
     * Create infinite loop for main instance application using most efficient way
     * @param runnable Comment to be executor
     */
    public void startLoop(final Runnable runnable) {

        this.running.set(true);

        final Runnable command = () -> {

            this.mappedArguments.clear();
            final String[] buffer = this.COMMAND_LINE_INPUT.nextLine().split(" ");
            for (int i = 0; i < buffer.length; i++) this.mappedArguments.put(i, buffer[i]);
            runnable.run();

        };

        while (this.isRunning()) command.run();

    }

    /**
     * Stop infinite loop
     *
     * @param systemTermination If true, system will be fully terminated
     */
    public void stopLoop(boolean systemTermination) {

        this.mappedArguments.clear();
        this.running.set(false);
        if (systemTermination) System.exit(0);

    }

    /**
     * Send message to console using {@code java.io.PrintStream} and {@code java.lang.StringBuilder} for better style
     * <p><string>Note: </string></p>Using {@code java.lang.StringBuilder} since class is thread-safe
     * @param messages Messsage as String Array for sending multiply messages at once
     */
    public void sendMessage(String... messages) {
        for (String message : messages)
            System.out.println("[CLI] " + message);

    }

    /**
     * Check if loop is running
     *
     * @return true if loop is running
     */
    public boolean isRunning() {
        return this.running.get();
    }

    /**
     * Check whether a sub-command is used
     *
     * @param command Command string inserted in console
     * @param excludedRange Range till an excluded position in commands string
     * @return true if sub-command is found
     */
    public boolean isSubCommand(String command, int excludedRange) {
        if (this.mappedArguments.size() <= excludedRange) return false;
        return command.equalsIgnoreCase(String.join(" ", this.mappedArguments.values().stream().toList().subList(0, excludedRange)));
    }

    /**
     * Check whether a full command line is contained in console
     *
     * @param commands Commands to check
     * @return true if contained
     */
    public boolean isCommand(String... commands) {
        for  (String command : commands)
            if (command.equalsIgnoreCase(this.toString())) return true;

        return false;
    }

    /**
     * Checking whether index i is in list
     *
     * @param i Index
     * @return True if contained
     */
    public boolean contains(int i) {
        return this.mappedArguments.containsKey(i);
    }

    /**
     * Checking whether var is in list
     *
     * @param var Variable
     * @return True if contained
     */
    public boolean contains(String var) {
        return this.mappedArguments.containsValue(var);
    }

    /**
     * Get value of specific index
     *
     * @param i Index
     * @return Value at pos i
     */
    public String valueOf(int i) {
        assert this.contains(i) : "Value not in map";
        return this.mappedArguments.get(i);
    }

    /**
     * Convert a var to a specific number format
     * @param i Index of var
     * @param mappingType Type for convertion
     * @return A number
     */
    public Number valueOfAs(int i, ConsoleArgumentMapping mappingType) {

        try {

            final String var  = this.valueOf(i);

            switch (mappingType) {
                case INT -> {
                    return Integer.parseInt(var);
                }
                case FLOAT -> {
                    return Float.parseFloat(var);
                }
                case DOUBLE -> {
                    return Double.parseDouble(var);
                }
            }

        } catch (NumberFormatException ignored) {}

        return null;
    }

    /**
     * Get index of variable
     *
     * @param var Variable
     * @return Index of variable
     */
    public int indexOf(String var) {
        assert this.contains(var) : "Index not in map";
        return this.mappedArguments.entrySet().stream().filter(entry -> entry.getValue().equals(var)).findFirst().orElseThrow().getKey();
    }

    /**
     * Checking whether variable is instanceOf mappingType
     *
     * @param var Variable
     * @param mappingType Existing data types in Java_22
     * @return True if instance of
     */
    public boolean instanceOf(Object var, ConsoleArgumentMapping mappingType) {

        try {

            switch (mappingType) {
                case INT -> Integer.parseInt(var.toString());
                case FLOAT -> Float.parseFloat(var.toString());
                case DOUBLE -> Double.parseDouble(var.toString());
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    /**
     * Checking whether index is out of range of list
     *
     * @param i Index to check
     * @return True if out of range
     */
    public boolean outOfBounds(int i) {
        return i >= this.mappedArguments.size();
    }

    /**
     * Size of argument map
     *
     * @return Size
     */
    public int size() {
        return this.mappedArguments.size();
    }

    /**
     * Get line buffer from input
     *
     * @return Buffer content converted to single String
     */
    @Override
    public String toString() {
        return this.mappedArguments.values().stream().map(Object::toString).collect(Collectors.joining(" "));
    }

}